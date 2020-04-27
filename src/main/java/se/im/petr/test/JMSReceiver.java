package se.im.petr.test;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Receive message from AOM using CSF JMS
 * 
 * @author petr
 */
public class JMSReceiver {
	
	/* JMS Implementation Class Names
	 */
	protected static final String QCF_CLASS_NAME = "se.im.csf.linkexpress.jms.LinkExpressConnectionFactory";
	protected static final String QUEUE_CLASS_NAME = "se.im.csf.linkexpress.jms.LinkExpressQueue";

	protected ConnectionFactory qcf;
	protected Destination destination;
	
	public String host;
	
	public int port;
	
	public String userId;
	
	public String pollInterval;

	/**
	 * Explicit no-arg constructor
	 */
	public JMSReceiver() {
	}
	
	/**
	 * Receive a message
	 * @param secs 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String receiveMessage(int secs) throws Exception {
		try {
			/*
			 * Create a Connection and a Session
			 */
			Connection connection = qcf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			/*
			 * Create a Message Producer using our Destination and send the message
			 */
			MessageConsumer messageConsumer = session.createConsumer(destination, "MESSAGETYPE = 'CZ0104'");
			Message message = messageConsumer.receive(secs*1000L);
			if (message == null) {
				return null;
			}
			else if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				return textMessage.getText();
			}
			else {
				throw new RuntimeException("what the heck!");
			}
		}
		catch (Exception e) {
			throw new RuntimeException("cannot receive: " + e.getMessage());
		}
	}

	/*** --- for testing ***/


	/**
	 * JMS Receiver constructor for testing only
	 * 
	 * @param host
	 * @param port
	 * @param userId
	 */
	public JMSReceiver(String host, int port, String userId) {
		this.host = host;
		this.port = port;
		this.userId = userId;
		createConnectionFactory();
	}
	

	/**
	 * Create Connection Factory
	 */
	public void createConnectionFactory() {
		try {
			/*
			 * Create a JMS Connection Factory and Destination and configure
			 * them using reflection
			 */
			qcf = (ConnectionFactory) Class.forName(QCF_CLASS_NAME).newInstance();

			qcf.getClass().getField("host").set(qcf, host);			
			qcf.getClass().getField("port").set(qcf, port);
			qcf.getClass().getField("userid").set(qcf, userId);
			destination = (Destination) Class.forName(QUEUE_CLASS_NAME).newInstance();

//			destination = Destination.class.getConstructor().newInstance();
			destination.getClass().getField("qname").set(destination, "myqueue");
		}
		catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			throw new RuntimeException("creating Connection Factory and Queue", e);
		}
	}
}
