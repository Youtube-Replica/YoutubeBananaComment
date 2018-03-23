package model;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;

import com.arangodb.entity.BaseDocument;
import com.arangodb.util.MapBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class Comment {

    public static String getCommentByID(int id) {
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "comments";
        JSONObject commentObjectM = new JSONObject();
        try {
            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument("" + id,
                    BaseDocument.class);

            commentObjectM.put("Video ID",myDocument.getAttribute("video_id"));
            commentObjectM.put("Text",myDocument.getAttribute("text"));
            commentObjectM.put("Likes",myDocument.getAttribute("likes"));
            commentObjectM.put("Dislikes",myDocument.getAttribute("dislikes"));
            commentObjectM.put("Channel ID",myDocument.getAttribute("user"));
            commentObjectM.put("Mentions IDs",myDocument.getAttribute("mentions"));
            commentObjectM.put("Reply IDs",myDocument.getAttribute("replies"));

        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }
        return commentObjectM.toString();
    }

    public static String getCommentsByVideoID(int id) {
        System.out.println("ID: " + id);
        ArangoDB arangoDB = new ArangoDB.Builder().build();
        String dbName = "scalable";
        String collectionName = "comments";
        JSONObject allCommentsReturned = new JSONObject();

        try {
            String query = "FOR doc IN comments\n" +
                    "        FILTER doc.`video_id` == @value\n" +
                    "        RETURN doc";
            Map<String, Object> bindVars = new MapBuilder().put("value", id).get();
            System.out.println("Bind vars:");
            System.out.println(bindVars.toString());
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, bindVars, null,
                    BaseDocument.class);

            if(cursor.hasNext()) {
                BaseDocument cursor2 = null;
                JSONArray searchArray = new JSONArray();
                int new_id=0;
                for (; cursor.hasNext(); ) {
                    JSONObject searchObjectM = new JSONObject();
                    cursor2 = cursor.next();
                    BaseDocument myDocument2 = arangoDB.db(dbName).collection(collectionName).getDocument(cursor2.getKey(),
                            BaseDocument.class);
                    new_id = Integer.parseInt(cursor2.getKey());

//                    searchObjectM.put("Video ID",myDocument2.getAttribute("video_id"));
                    searchObjectM.put("Video ID",new_id);
                    searchObjectM.put("Text",myDocument2.getAttribute("text"));
                    searchObjectM.put("Likes",myDocument2.getAttribute("likes"));
                    searchObjectM.put("Dislikes",myDocument2.getAttribute("dislikes"));
                    searchObjectM.put("Channel ID",myDocument2.getAttribute("user"));
                    searchObjectM.put("Mentions IDs",myDocument2.getAttribute("mentions"));
                    searchObjectM.put("Reply IDs",myDocument2.getAttribute("replies"));

                    searchArray.add(searchObjectM);
                }
                allCommentsReturned.put("VideoID: "+id,searchArray);
            }

        } catch (ArangoDBException e) {
            System.err.println("Failed to get document: myKey; " + e.getMessage());
        }
        return allCommentsReturned.toString();
    }

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
