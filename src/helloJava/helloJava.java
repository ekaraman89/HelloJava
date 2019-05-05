package helloJava;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class helloJava {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello Java\n\n");

		

		String result = executePost("http://akillihaberokuyucu.somee.com/Ak%C4%B1ll%C4%B1Haber.asmx/Kategori", "");

		Categories cat = new Categories(result);		

		System.out.println("Model oluþturuluyor...");
		String trainFile = "model.arff";
		Instances train = getInstance(trainFile);		
		int lastIndex = train.numAttributes() - 1;
		train.setClassIndex(lastIndex);		
		System.out.println("Eðitim seti okundu...");
		
		StringToWordVector stwv = new StringToWordVector();
		stwv.setInputFormat(train);
		train = weka.filters.Filter.useFilter(train, stwv);
		System.out.println("String to word Vektor iþlemi yapýldý...");
		
		System.out.println("Naive Bayes modeli oluþturuluyor...");
		NaiveBayes bayes = new NaiveBayes();
		bayes.buildClassifier(train);
		System.out.println("Model oluþturuldu");
		
		String testFile = "Files\\WithoutStopWordOriginal.arff";
		String originalData ="Original.txt";
		while(true)	{
			
			if(new File(testFile).exists() && !new File("lock.txt").exists())	{
				List<String> allLines = Files.readAllLines(Paths.get(originalData));
				System.out.println("Test verisi okumasý yapýlýyor..");
				
				Instances test = getInstance(testFile);
				test.setClassIndex(lastIndex);
				test = weka.filters.Filter.useFilter(test, stwv);
				System.out.println("Test verisi üzerinde String to word Vektor iþlemi yapýldý...");		
				
				System.out.println("Sýnýflandýr iþlemine baþlanýyor");
				for (int i = 0; i < test.numInstances(); i++) {
					double index = bayes.classifyInstance(test.instance(i));
					String className = train.attribute(0).value((int) index);
					System.out.println("Sýnýflandýrma Sonucu: " + className);
					String urlParameters = "yazarid=" + URLEncoder.encode("1", "UTF-8") + "&kategoriid="
							+ URLEncoder.encode(cat.getCategoryID(className), "UTF-8") + "&baslik="
							+ URLEncoder.encode("Hello Java - " + className, "UTF-8") + "&icerik="
							+ URLEncoder.encode(allLines.get(i), "UTF-8");
					executePost("http://akillihaberokuyucu.somee.com/Ak%C4%B1ll%C4%B1Haber.asmx/AddMakale", urlParameters);
		
				}
				System.out.println("Sýnýflandýrma iþlemi tamamlandý...\n\n\n--------------------------------");
				new File(testFile).delete();
				new File(originalData).delete();
			}
		}
	}

	private static Instances getInstance(String fileName) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		Instances instance = new Instances(reader);
		reader.close();

		return instance;
	}

	public static String executePost(String targetURL, String urlParameters) {
		HttpURLConnection connection = null;

		try {
			// Create connection
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static Document loadXMLString(String response) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(response));

		return db.parse(is);
	}

	public static List<String> getFullNameFromXml(String response, String tagName) throws Exception {
		Document xmlDoc = loadXMLString(response);
		NodeList nodeList = (xmlDoc).getElementsByTagName(tagName);
		List<String> ids = new ArrayList<String>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node x = nodeList.item(i);
			ids.add(x.getFirstChild().getNodeValue());
			System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
		}
		return ids;
	}

	
}
