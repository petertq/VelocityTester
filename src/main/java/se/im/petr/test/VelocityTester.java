package se.im.petr.test;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

public class VelocityTester {

	public String createMessage(String templateName, VelocityContext context) throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		Properties properties = new Properties();
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("velocity.properties")) {
			properties.load(is);
			velocityEngine.init(properties);
			Template template = velocityEngine.getTemplate(templateName);
			StringWriter stringWriter = new StringWriter();
			template.merge(context, stringWriter);
			return stringWriter.toString();
		}
	}

	public void test1() throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("nyckel", "min nyckel");
		String message = createMessage("test1.vm", context);
		System.out.println("transformed message:\n" + message);
	}

	public void test2() throws Exception {
		VelocityContext context = new VelocityContext();
		SAXBuilder builder = new SAXBuilder();
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("AXESP0104.xml")) {
			Document document = builder.build(is);
			Element root = document.getRootElement();
			Namespace ns = Namespace.getNamespace("http://www.aftobbladet.se");
			context.put("root", root);
			context.put("ns", ns);
			String message = createMessage("transform.vm", context);
			System.out.println("transformed message:\n" + message);
		}
	}

	public void test3() throws Exception {
		JMSReceiver receiver = new JMSReceiver("midas", 1234, "edi");
		String receivedMessage = receiver.receiveMessage(10);
		VelocityContext context = new VelocityContext();
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new StringReader(receivedMessage));
		Element root = document.getRootElement();
		Namespace ns = Namespace.getNamespace("http://www.aftobbladet.se");
		context.put("root", root);
		context.put("ns", ns);
		String message = createMessage("transform.vm", context);
		System.out.println("transformed message:\n" + message);
	}

	public static void main(String[] args) throws Exception {
		new VelocityTester().test2();
	}
}
