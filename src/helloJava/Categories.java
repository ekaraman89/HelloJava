package helloJava;

import java.util.ArrayList;
import java.util.List;


public class Categories {
	private String CategoryID;

	private String CategoryName;

	private static String getWritersUrl = Helpers.mainUrl+"Kategori";
	
	public static String getCategoryID(String CategoryName) throws Exception {
		String data =Helpers.executePost(getWritersUrl, "");
		for(Categories category : getCategories(data)) {
	        if(category.CategoryName.equals(CategoryName)) {
	            return category.CategoryID;
	        }
	    }
	    return null;
	}

	private static List<Categories> getCategories(String xml) throws Exception {
		List<String> IDs =Helpers.getFullNameFromXml(xml, "KategoriID");
		List<String> Names =Helpers.getFullNameFromXml(xml, "KategoriAdi");
		List<Categories> lst = new ArrayList<Categories>();
		for (int i = 0; i < IDs.size(); i++) {
			Categories cat = new Categories();
			cat.CategoryID = (IDs.get(i));
			cat.CategoryName = (Names.get(i));
			lst.add(cat);
		}
		return lst;
	}

}
