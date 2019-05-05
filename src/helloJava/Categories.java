package helloJava;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Categories {
	private String CategoryID;

	private String CategoryName;

	private List<Categories> Categories;
	
	public Categories() {}
	public Categories(String xml) throws Exception {
		Categories = getCategories(xml);		
	}
	
	public String getCategoryID(String CategoryName) {
		for(Categories category : Categories) {
	        if(category.CategoryName.equals(CategoryName)) {
	            return category.CategoryID;
	        }
	    }
	    return null;
	}

	private List<Categories> getCategories(String xml) throws Exception {
		List<String> IDs = getFullNameFromXml(xml, "KategoriID");
		List<String> Names = getFullNameFromXml(xml, "KategoriAdi");
		List<Categories> lst = new ArrayList<Categories>();
		for (int i = 0; i < IDs.size(); i++) {
			Categories cat = new Categories();
			cat.CategoryID = (IDs.get(i));
			cat.CategoryName = (Names.get(i));
			lst.add(cat);
		}
		return lst;
	}

	private Document loadXMLString(String response) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(response));

		return db.parse(is);
	}

	private List<String> getFullNameFromXml(String response, String tagName) throws Exception {
		Document xmlDoc = loadXMLString(response);
		NodeList nodeList = (xmlDoc).getElementsByTagName(tagName);
		List<String> ids = new ArrayList<String>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node x = nodeList.item(i);
			ids.add(x.getFirstChild().getNodeValue());
		}
		return ids;
	}

}
