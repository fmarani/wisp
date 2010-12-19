package org.flagzeta

import org.restlet.{Client,Component,Restlet};
import org.restlet.data._

import scala.xml.XML
import scala.xml.Elem
import org.apache.commons.logging.{Log,LogFactory}

import org.apache.commons.codec.digest.DigestUtils

import java.util.logging.LogManager

object Constants{
    val callback= "hub.callback"
    val mode = "hub.mode"
    val subscribe ="subscribe"
    val unsubscribe ="unsubscribe"
    val topic = "hub.topic"
    val verify = "hub.verify"
    val lease_seconds = "hub.lease_seconds"
    val secret = "hub.secret"
    val verify_token = "hub.verify_token"
    val sync = "sync"
    val async = "async"
    val challenge = "hub.challenge"
}

class SubscriberRestlet(val subscriber: Subscriber) extends Restlet {
    val log = LogFactory.getLog(getClass)
    
	/**
	 * Handle subscribe / unsubscribe challenges from a hub.
	 */
	def doGET(request: Request, response: Response) {
		val params = request.getResourceRef.getQueryAsForm
		val mode = params.getFirstValue(Constants.mode)
		val topic = params.getFirstValue(Constants.topic)
		val lease_seconds = params.getFirstValue(Constants.lease_seconds).toInt

		log.info("Received hub challenge:\n-Mode: " + mode + "\n-Topic: " + topic + "\n-Lease: " + lease_seconds + " seconds")

		// TODO: add appropriate checks to ensure the subscriber does not blindly accept challenges from hub.
		response.setEntity(params.getFirstValue(Constants.challenge),MediaType.TEXT_PLAIN)

		mode match{
			case Constants.subscribe => subscriber.subscribed(topic)
			case Constants.unsubscribe => subscriber.unsubscribed(topic)
		}

		log.info("Acknowledged hub challenge.")
	}

	/*
	 * Handle content notification from a hub.
	 */
	def doPOST(request: Request, response: Response){
		log.info("Incoming POST request: " + request.getResourceRef)
		val entity = request.getEntity
		if (entity.getMediaType == MediaType.APPLICATION_ATOM_XML){
			response.setStatus(Status.SUCCESS_OK)
			val atom = XML.load(entity.getStream)
			subscriber.contentPublished(atom)
		} else {
			log.error("Received hub notification containing unsupported payload of type " + entity.getMediaType + "\n" + entity.getText)
			response.setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE)
		}
	}

	override def handle(request : Request, response: Response) = {
		request.getMethod match {
		    case Method.GET => doGET(request,response) 
		    case Method.POST => doPOST(request, response)
		    //case Method.DELETE => doDELETE(request, response)
		}
	}
}

case class Feed(val topicURL : String, val hubURL : String, title : String) {
	val id = DigestUtils.md5Hex(topicURL)
}

class Subscriber(val hostname:String, val port : Int) {
  val httpClient = new Client(Protocol.HTTP);
  val pushUri = "push"
  val callbackUrl = "http://" + hostname + ":" + port + "/" + pushUri
  val subscriberRestlet = new SubscriberRestlet(this)
  var pendingSubscriptions : Set[Feed] = Set()
  var activeSubscriptions : Set[Feed] = Set()
  val log = LogFactory.getLog(getClass)

  startServer
 
  def startServer { 
	  val publicRoot = new Component
	  publicRoot.getLogService.setEnabled(false)
	  publicRoot.getServers.add(Protocol.HTTP, port)
	  publicRoot.getDefaultHost.attach("/" + pushUri + "/{ID}", subscriberRestlet)
	  publicRoot.start
  }

  /**
   * Discover PubSubHubBub protocol information from an Atom feed.
   * Return an optional Feed object containing one of the hub URLs and the topic URL or nothing is the feed doed not support PubSubHubBub.
   */
  def discover(atomURL: String): Option[Feed] = {
    val response = httpClient.handle(new Request( Method.GET,atomURL))
    if (response.getStatus.isSuccess){
      val xml = XML.load (response.getEntity.getStream)
      // Look for xml nodes containing a hub URL and a topic URL
      val nodes = List("self","hub").map (v => xml \"link" find ( _\"@rel" == v))
      (nodes) match {
        case List(Some(selfNode),Some(hubNode)) => Some( Feed (selfNode \ "@href" text , hubNode \ "@href" text , xml \ "title" text))
        case _ => None
      }
    }
    else None
  }
  
  /**
   * Subscribe to a feed given its URL.
   * Will complain if the feed is not PubSubHubBub enabled.
   */
  def subscribe(feedURL: String) {
    discover(feedURL) match {
      case Some(feed) => subscribe(feed)
      case None => log.error("Unable to fetch the hub or topic URL from the feed " + feedURL)
    }
  }
  
  /**
   * Subscribe to a PuSH enabled feed.
   */
  def subscribe(feed: Feed) {
	pendingSubscriptions = pendingSubscriptions + feed
	val callbackCompleteUrl = callbackUrl + "/" + feed.id
	issueRequest(feed.topicURL, feed.hubURL, callbackCompleteUrl, Constants.subscribe)
  }

  /**
   * Subscribe or unsubscribe to a PuSH enabled feed.
   */
  def unsubscribe(feed : Feed) {
	pendingSubscriptions = pendingSubscriptions + feed
	val callbackCompleteUrl = callbackUrl + "/" + feed.id
	issueRequest(feed.topicURL, feed.hubURL, callbackCompleteUrl, Constants.unsubscribe)
  }

  /**
   * Send a request to the hub to subscribe or unsubscribe a feed.
   */
  private def issueRequest(topicURL : String, hubURL : String, callbackURL : String, mode : String){
	val form = new Form
	form.add(Constants.topic, topicURL)
	form.add(Constants.mode, mode)
	form.add(Constants.callback, callbackURL)
	form.add(Constants.verify, Constants.sync)
	val response = httpClient.handle(new Request(Method.POST, hubURL, form.getWebRepresentation))
	if (!response.getStatus.isSuccess) log.error("Unable to " + mode + " to topic " + topicURL + ", HTTP status: " + response.getStatus.toString)
  }
  
  /**
   * Notification that a hub has accepted a request to subscribe to a feed.
   */
  def subscribed(topicURL: String){
	val found = pendingSubscriptions.find(_.topicURL == topicURL)
	found match {
		case Some(subscription) => activeSubscriptions = activeSubscriptions + subscription
		case _ => log.warn("Subscription request with unrecognized feed: " + topicURL)
	}
  }

  /**
   * Notification that a hub has accepted a request to unsubscribe from a feed.
   */
  def unsubscribed(topicURL: String){
	activeSubscriptions = activeSubscriptions.filter( _.topicURL != topicURL)
  }
  
  /*
   * Notication of content publication from a hub.
   */
  def contentPublished(atom: Elem){
	// act on atom notification
  }
}
object Main {
def main ( args: Array[String]) : Unit = {
//LogManager.getLogManager.readConfiguration(getClass.getResourceAsStream("/log.properties"))
new Subscriber("localhost", 8082)
println ("The subscriber service is up.")
}
}
