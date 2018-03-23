package model;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;

import com.arangodb.entity.BaseDocument;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Comment {

    public static String getCommentByID(int id) {
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "Comments";
        JSONObject commentObjectM = new JSONObject();
        JSONArray commentArray = new JSONArray();
        //Read Document
        //haygeely id harod b list ids
        try {
            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("" + id,
                    BaseDocument.class);

            commentObjectM.put("VideoID",myDocument.getAttribute("video_id "));
            commentObjectM.put("Text",myDocument.getAttribute("text"));
            commentObjectM.put("Likes",myDocument.getAttribute("likes"));
            commentObjectM.put("Dislikes",myDocument.getAttribute("dislikes"));
            commentObjectM.put("ChannelID",myDocument.getAttribute("user"));

            ArrayList<Integer> mentionsArray = new ArrayList<>();
            mentionsArray = (ArrayList) myDocument.getAttribute("mentions");
            ArrayList<JSONObject> mentionsNamesArray = new ArrayList<>();

            for (int i = 0; i < mentionsArray.size(); i++) {
                try {
                    BaseDocument myDocument2 = arangoDB.db(dbName).collection("Channels").getDocument("" + mentionsArray.get(i),
                            BaseDocument.class);
                    JSONObject channelObject = new JSONObject();
                    JSONObject info = new JSONObject();
                    info.put("Info",myDocument2.getAttribute("info"));
                    String name = ""; //info.get("name");
                    String profpic = ""; //info.get("profile_pic");
                    channelObject.put("Name", name);
                    channelObject.put("Profile Picture", profpic);
                    mentionsNamesArray.add(channelObject);
                }
                catch (ArangoDBException e) {
                    System.err.println("Failed to get document: myKey; " + e.getMessage());
                }
            }
            commentObjectM.put("mentions",mentionsNamesArray);
        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }


        return commentObjectM.toString();

    }
    //Comment for Videos by title and for Channel by name
//    public static String getSearch(String s) {
//        ArangoDB arangoDB = new ArangoDB.Builder().build();
//        String dbName = "scalable";
//
//        //First get by channel name
//        String collectionName = "Comments";
//        JSONObject searchObjectTotal = new JSONObject();
//
//        System.out.println("String we got:" + s);
//        try {
//            String query = "FOR doc IN Comments\n" +
//                    "        FILTER doc.`Name` == @value\n" +
////                    "        FILTER CONTAINS(doc.`Name`, @value)" +
//                    "        RETURN doc";
//            Map<String, Object> bindVars = new MapBuilder().put("value", s).get();
//
//            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
//                    BaseDocument.class);
//            if(cursor.hasNext()) {
//                BaseDocument cursor2 = null;
//                JSONArray searchArray = new JSONArray();
//                int id=0;
//                for (; cursor.hasNext(); ) {
//                    JSONObject searchObjectM = new JSONObject();
//                    cursor2 = cursor.next();
//                    BaseDocument myDocument2 = arangoDB.db(dbName).collection(collectionName).getDocument(cursor2.getKey(),
//                            BaseDocument.class);
//                    id= Integer.parseInt(cursor2.getKey());
//                    searchObjectM.put("ID", id);
//                    searchObjectM.put("Name", myDocument2.getAttribute("Name"));
//                    searchObjectM.put("Category", myDocument2.getAttribute("Category"));
//                    searchObjectM.put("Profile Picture", myDocument2.getAttribute("ProfilePicture"));
//                    searchArray.add(searchObjectM);
//                }
//                searchObjectTotal.put("Channel "+id,searchArray);
//            }
////            else{
//                JSONArray searchArray = new JSONArray();
//                query = "FOR doc IN Videos\n" +
//                       // "        FILTER doc.`title` like @value\n" +
//                        "        FILTER CONTAINS(doc.`title`, @value)" +
//                        "        RETURN doc";
//                bindVars = new MapBuilder().put("value", s).get();
//
//                cursor = arangoDB.db(dbName).query(query, bindVars, null,
//                        BaseDocument.class);
//                if(cursor.hasNext()) {
//                    BaseDocument cursor2=null;
//                    int id=0;
//                    for (; cursor.hasNext(); ) {
//                        cursor2 = cursor.next();
//                        System.out.println(cursor2.getKey());
//                        JSONObject searchObjectM = new JSONObject();
//                        BaseDocument myDocument2 = arangoDB.db(dbName).collection("Videos").getDocument(cursor2.getKey(),
//                                BaseDocument.class);
//                        id= Integer.parseInt(cursor2.getKey());
//                        searchObjectM.put("ID", id);
//                        searchObjectM.put("Likes", Integer.parseInt(""+myDocument2.getAttribute("likes")));
//                        searchObjectM.put("Dislikes", Integer.parseInt(""+myDocument2.getAttribute("dislikes")));
//                        searchObjectM.put("Views", Integer.parseInt(""+myDocument2.getAttribute("views")));
//                        searchObjectM.put("Thumbnail", myDocument2.getAttribute("thumbnail"));
//                        searchObjectM.put("Title", myDocument2.getAttribute("title"));
//                        searchObjectM.put("Category", myDocument2.getAttribute("category"));
//                        searchObjectM.put("Duration", Integer.parseInt(""+myDocument2.getAttribute("duration")));
//                        searchObjectM.put("Date Created", myDocument2.getAttribute("date_created"));
//                        searchArray.add(searchObjectM);
//                    }
//                    searchObjectTotal.put("Video "+id,searchArray);
//                }
//                else{
//                    searchObjectTotal.put("Not found ",s);
//                }
////            }
//        } catch (ArangoDBException e) {
//            System.err.println("Failed to execute query. " + e.getMessage());
//        }
//        System.out.println("Comment Object" + searchObjectTotal.toString());
//        return searchObjectTotal.toString();
//
//
//    }

    public static String createComment(int video_id, String text, JSONArray likes, JSONArray dislikes, int user_id, JSONArray mentions, JSONArray replies){
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "comments";
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("video_id",video_id);
        myObject.addAttribute("text",text);
        myObject.addAttribute("likes",likes);
        myObject.addAttribute("dislikes",dislikes);
        myObject.addAttribute("user",user_id);
        myObject.addAttribute("mentions",mentions);
        myObject.addAttribute("replies",replies);
        try {
            arangoDB.db(dbName).collection(collectionName).insertDocument(myObject);
            System.out.println("Document created");
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }
        return "Document created";
    }



    public static String deleteCommentByID(int id){
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "comments";
        try {
        arangoDB.db(dbName).collection(collectionName).deleteDocument(""+id);
        }catch (ArangoDBException e){
            System.err.println("Failed to delete document. " + e.getMessage());
        }
        return "Comment Deleted";
    }
    public static String deleteReplyByID(int comment_id,int reply_id){
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "comments";
        BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("" + comment_id,
                BaseDocument.class);
        try {
    ArrayList<JSONObject> replies = new ArrayList<>();
        replies = (ArrayList<JSONObject>) myDocument.getAttribute("replies");
        replies.remove(reply_id);
        myDocument.updateAttribute("replies", replies);
        arangoDB.db(dbName).collection(collectionName).deleteDocument("" + comment_id);
        arangoDB.db(dbName).collection(collectionName).insertDocument(myDocument);
        }catch (ArangoDBException e){
            System.err.println(e.getErrorMessage());
        }
        return "Reply Deleted";
        }

    public static String updateComment(int comment_id ,int video_id, String text, JSONArray likes, JSONArray dislikes, int user_id, JSONArray mentions, JSONArray replies){
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "comments";
        BaseDocument myObject = arangoDB.db(dbName).collection(collectionName).getDocument("" + comment_id,
                BaseDocument.class);

        myObject.updateAttribute("video_id",video_id);
        myObject.updateAttribute("text",text);
        myObject.updateAttribute("likes",likes);
        myObject.updateAttribute("dislikes",dislikes);
        myObject.updateAttribute("user",user_id);
        myObject.updateAttribute("mentions",mentions);
        myObject.updateAttribute("replies",replies);
        try {
            arangoDB.db(dbName).collection(collectionName).deleteDocument(""+comment_id);
            arangoDB.db(dbName).collection(collectionName).insertDocument(myObject);
            System.out.println("Document created");
        } catch (ArangoDBException e) {
            System.err.println("Failed to create document. " + e.getMessage());
        }
        return "Document updated";
    }

    }
