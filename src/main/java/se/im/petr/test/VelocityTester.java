package se.im.petr.test;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;
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

	public String spaces(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		return sb.toString();
	}
	
	public void listContent(List<Content> contentList, int level) {
		for (Content content : contentList) {
			CType ctype = content.getCType();
			switch (ctype) {
			case Element:
				System.out.println(spaces(level) + "Element: " + content.toString());
				if (content instanceof Element) {
					Element element = (Element) content;
					listContent(element.getContent(), level+1);
				}
				break;
			case Text:
				if (content instanceof Text) {
					Text text = (Text) content;
					System.out.println(spaces(level) + "Text: " + text.getText().trim());
				}
				break;
			}
		}
	}
	public void test2() throws Exception {
		VelocityContext context = new VelocityContext();
		SAXBuilder builder = new SAXBuilder();
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("AXESP0104.xml")) {
			Document document = builder.build(is);
			List<Content> contentList = document.getContent();
//			listContent(contentList, 0);
			Element root = document.getRootElement();
			Namespace ns = Namespace.getNamespace("http://www.aftobbladet.se");
			context.put("root", root);
			context.put("ns", null);
			String message = createMessage("transform.vm", context);
			System.out.println("transformed message:\n" + message);
		}
	}

	public void test3() throws Exception {
		JMSReceiver receiver = new JMSReceiver("midas", 5311, "edi");
		String receivedMessage = receiver.receiveMessage(10);
		System.out.println("received message: " + receivedMessage);
		if (receivedMessage == null) {
			System.out.println("no message");
			return;
		}
		VelocityContext context = new VelocityContext();
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new StringReader(receivedMessage));
		Element root = document.getRootElement();
		Namespace ns = Namespace.getNamespace("http://www.aftobbladet.se");
		context.put("root", root);
		context.put("ns", null); // no namespace!
		String message = createMessage("transform.vm", context);
		System.out.println("transformed message:\n" + message);
	}

	public static void main(String[] args) throws Exception {
		new VelocityTester().test2();
	}
}
