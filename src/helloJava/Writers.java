package helloJava;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Writers {
	
	private String _writerID;

	private String _writerUseName;	

	private static String getWritersUrl = Helpers.mainUrl+"Yazar";	
	
	public static String getWriterID(String WriterUserName) throws Exception {
		String data =Helpers.executePost(getWritersUrl, "");
		String result = checkWriter(data,WriterUserName); //getWriter(WriterUserName);//
		if(result==null)
		{
			
			String urlParameters = "yazaradi=" + URLEncoder.encode(WriterUserName, "UTF-8")+"&kullaniciadi=" + URLEncoder.encode(WriterUserName, "UTF-8");
			Helpers.executePost(Helpers.mainUrl+"AddYazar", urlParameters);
			data =Helpers.executePost(getWritersUrl, "");
			result = checkWriter(data,WriterUserName);
			//result = getWriter(WriterUserName);
		}
		
	    return  result;
	}
	
	private static String checkWriter(String data, String WriterUserName) throws Exception {
		for(Writers writer : getWriters(data)) {
	        if(writer._writerUseName.equals(WriterUserName.substring(1))||writer._writerUseName.equals(WriterUserName)) {
	            return writer._writerID;
	        }
	    }
		return null;
	}
	
	
	private static List<Writers> getWriters(String xml) throws Exception {
		List<String> IDs =Helpers.getFullNameFromXml(xml, "YazarID");
		List<String> Names =Helpers.getFullNameFromXml(xml, "YazarAdi");
		List<Writers> lst = new ArrayList<Writers>();
		for (int i = 0; i < IDs.size(); i++) {
			Writers writer = new Writers();
			writer._writerID = IDs.get(i);
			writer._writerUseName = Names.get(i).replace("﻿","");
			lst.add(writer);
			//System.out.println(writer._writerUseName);
		}
		return lst;
	}
	
	/*
	private static String getWriter(String userName) throws Exception
	{
		String urlParameters = "kullaniciadi="+userName.trim();
		String data =Helpers.executePost(Helpers.mainUrl+"YazarArama",urlParameters );
		List<String>result = Helpers.getFullNameFromXml(data, "YazarID");
		if(result!=null) 
			return result.get(0);
		else return null;
	}*/

}
