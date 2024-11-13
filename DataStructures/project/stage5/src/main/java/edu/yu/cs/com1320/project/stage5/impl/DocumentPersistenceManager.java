package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.net.URI;
import java.io.FileWriter;
import java.nio.file.*;
import java.net.URISyntaxException;
//import java.nio.Files;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.*;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private String dir;
    //private JsonElement tempJson = null;

    private class DocSerializer<DocumentImpl> implements com.google.gson.JsonSerializer<DocumentImpl>{
        @Override
        public JsonElement serialize(DocumentImpl docImpl, Type typeOfDoc, JsonSerializationContext context){

            //String tempString = docImpl.getKey().toString();
            //if(docImpl.getDocumentTxt()!=null){
            //    tempString+=":";
            //    tempString+=docImpl.getDocumentTxt();
            //    tempString+=":";
            //    tempString+=":";
            //    temp.add("wordCount", docImpl.getWordMap().toString());
            //}

            edu.yu.cs.com1320.project.stage5.impl.DocumentImpl tempDoc = (edu.yu.cs.com1320.project.stage5.impl.DocumentImpl)docImpl;

            /*Gson gson = new Gson();
            JsonArray tempArray = new JsonArray();
            tempArray.add(tempDoc.getKey().toString());
            //assert(tempArray.get(0).equals(gson.toJson(tempDoc.getKey())));
            //assert(tempArray.get(0)!=null);
            //URI uri = gson.fromJson(tempArray.get(0), URI.class);
            if(tempDoc.getDocumentTxt()!=null){
                tempArray.add(gson.toJson(tempDoc.getDocumentTxt()));
                tempArray.add(gson.toJson(tempDoc.getWordMap()));
            }
            else{
                tempArray.add(DatatypeConverter.printBase64Binary(tempDoc.getDocumentBinaryData()));
            }*/

            Gson gson = new Gson();
            JsonObject tempObj = new JsonObject();
            tempObj.addProperty("uri",tempDoc.getKey().toString());
            //assert(tempArray.get(0).equals(gson.toJson(tempDoc.getKey())));
            //assert(tempArray.get(0)!=null);
            //URI uri = gson.fromJson(tempArray.get(0), URI.class);
            if(tempDoc.getDocumentTxt()!=null){
                tempObj.addProperty("text",tempDoc.getDocumentTxt());
                tempObj.addProperty("wordMap",gson.toJson(tempDoc.getWordMap()));
            }
            else{
                tempObj.addProperty("text",DatatypeConverter.printBase64Binary(tempDoc.getDocumentBinaryData()));
            }
            
            //Gson gson = new Gson();
            //String tempMap = gson.toJson(docImpl.getWordMap());
            //String tempURI = gson.toJson(docImpl.getKey());
            //temp.add("1", docImpl.getKey().toString());
            //if(docImpl.getDocumentTxt()!=null){
            //    temp.add("text", docImpl.getDocumentTxt());
            //    temp.add("wordCount", docImpl.getWordMap().toString());
            //}
            //else{
            //    int x = 1;//add bytes
            //}
            //tempJson = tempArray;
            return tempObj;
            //return tempArray;
        }
    }

    private class DocDeserializer<DocumentImpl> implements com.google.gson.JsonDeserializer<DocumentImpl>{

        //deserialize(com.google.gson.JsonElement,java.lang.reflect.Type,com.google.gson.JsonDeserializationContext)

        @Override
        public DocumentImpl deserialize(com.google.gson.JsonElement json,java.lang.reflect.Type typeOfDoc,com.google.gson.JsonDeserializationContext context){
            Gson gson = new Gson();

            /*JsonArray jArray = (JsonArray)json;
            String uriString = gson.fromJson(jArray.get(0), String.class);
            URI uri = gson.fromJson(jArray.get(0), URI.class);
            //URI uri = gson.fromJson("http://java.sun.com/j2se/13", URI.class);
            //URI uri = new URI(temp);
            String temp = gson.fromJson(jArray.get(1), String.class);

            TypeToken<Map<String, Integer>> mapType = new TypeToken<Map<String, Integer>>(){};
            //String tempMap = "{\"key\": \"value\"}";
            Map<String, Integer> stringMap = gson.fromJson(jArray.get(2), mapType.getType());
            return (DocumentImpl) new edu.yu.cs.com1320.project.stage5.impl.DocumentImpl(uri, temp, stringMap);*/

            JsonObject jObj = (JsonObject)json;
            String uriString = gson.fromJson(jObj.get("uri"), String.class);
            //assert(uriString.equals("http://java.sun.com/j2se/13"));
            try{
                URI uri = new URI(uriString);
                //URI uri = gson.fromJson(jArray.get(0), URI.class);
                //URI uri = gson.fromJson("http://java.sun.com/j2se/13", URI.class);
                String temp = gson.fromJson(jObj.get("text"), String.class);
                TypeToken<Map<String, Integer>> mapType = new TypeToken<Map<String, Integer>>(){};
                //String tempMap = "{\"key\": \"value\"}";
                String mapString = gson.fromJson(jObj.get("wordMap"), String.class);
                if(mapString==null){
                    byte[] base64Decoded = DatatypeConverter.parseBase64Binary(temp);
                    return (DocumentImpl) new edu.yu.cs.com1320.project.stage5.impl.DocumentImpl(uri, base64Decoded);
                }
                Map<String, Integer> stringMap = gson.fromJson(mapString, mapType.getType());
                //Map<String, Integer> stringMap = gson.fromJson(jObj.get("wordMap"), mapType.getType());
                //assert(1==2);
                return (DocumentImpl) new edu.yu.cs.com1320.project.stage5.impl.DocumentImpl(uri, temp, stringMap);
            }
            catch(URISyntaxException e){
                throw new IllegalArgumentException("uri cannot be read");
            }
        }
    }

    public DocumentPersistenceManager(File baseDir){
        if(baseDir==null){
            this.dir=System.getProperty("user.dir");
        }
        else{
            this.dir=baseDir.getAbsolutePath();
        }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new DocSerializer()).setPrettyPrinting().create();
        String jsonString = gson.toJson(val);

        String tempDir = dir+uriToPath(uri);
        File tempFile = new File(tempDir);
        tempFile.getParentFile().mkdirs();
        //int count = tempDir.length();
        //for(int i=1; i<count;i++){
        //    if(tempDir.charAt(count-i)=='/'){
        //        count = i;
        //        break;
        //    }
        //}
        //String fileName = tempDir.substring(tempDir.length()-count) + ".json";
        //tempFile.createTempFile(tempDir.substring(tempDir.length()-count, tempDir.length()), ".json", tempFile.getParentFile());
        //tempFile.mkdirs();
        //tempFile = new File(fileName);
        //tempFile.createNewFile();
        FileWriter file = new FileWriter(tempFile);
        file.write(jsonString);
        file.close();
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new DocDeserializer()).setPrettyPrinting().create();
        //DocumentImpl temp = gson.fromJson(tempJson, DocumentImpl.class);

        String tempDir = dir+uriToPath(uri);
        //boolean isDir = new File(tempDir).mkdirs();
        //FileReader fileReader = new FileReader(tempDir);
        File tempFile = new File(tempDir);
        Scanner myReader = new Scanner(tempFile);

        String temp = "";
        while(myReader.hasNextLine()){
            temp+= myReader.nextLine();
        }
        return gson.fromJson(temp, DocumentImpl.class);
        //return null;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String tempDir = dir+uriToPath(uri);
        File tempFile = new File(tempDir);
        if(tempFile.delete()){
            return true;
        }

        /*try{
            Files.deleteIfExists(Paths.get(tempDir));
        }
        catch(Exception e){
            return false;
        }
        return true;
        */
        return false;
    }

    private String uriToPath(URI uri){
        String temp = uri.toString();
        String tempScheme = uri.getScheme();
        int count = tempScheme.length()+1;
        return temp.substring(count)+".json";
    }
}
