package helloJava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;


import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

public class helloJava {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello Java\n\n");		

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
		String lock = "lockjava.txt";
		while(true)	{
			
			if(new File(testFile).exists() && !new File("lock.txt").exists())	{
				new File(lock).createNewFile();
				Path path = Paths.get(lock);
				Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);

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
					
					String[] parameters =allLines.get(i).split("_@_");															
					String urlParameters = "yazarid=" + URLEncoder.encode(Writers.getWriterID(parameters[0]), "UTF-8") + "&kategoriid="
							+ URLEncoder.encode(Categories.getCategoryID(className), "UTF-8") + "&baslik="
							 + "&icerik="+ URLEncoder.encode(parameters[2], "UTF-8");
					
					Helpers.executePost(Helpers.mainUrl+"AddMakale", urlParameters);
		
				}
				System.out.println("Sýnýflandýrma iþlemi tamamlandý...\n\n\n--------------------------------");
				new File(testFile).delete();
				new File(originalData).delete();
				new File(lock).delete();
			}
		}
	}

	private static Instances getInstance(String fileName) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		Instances instance = new Instances(reader);
		reader.close();

		return instance;
	}
}
