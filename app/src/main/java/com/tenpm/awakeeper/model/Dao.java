package com.tenpm.awakeeper.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JiSoo on 2016-08-22.
 */
public class Dao {
    private Context context;
    private SQLiteDatabase database;

    public Dao(Context context) {
        this.context = context;

        database = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        try{
            String sql = "CREATE TABLE IF NOT EXISTS CarData  (ID integer primary key autoincrement,"
                    + "                                        gpsX double not null,"
                    + "                                        gpsY double not null,"
                    + "                                        velocity int not null,"
                    + "                                        angle double not null,"
                    + "                                        roadType varchar(50) not null);";
            database.execSQL(sql);
        } catch (Exception e){
            Log.e("test", "CREATE TABLE FAILED! -" + e);
            e.printStackTrace();
        }
    }

    public void insertJsonData(String jsonData){
        double gpsX;
        double gpsY;
        int velocity;
        double angle;
        String roadType;

        try{
            JSONArray jArr = new JSONArray(jsonData);

            for (int i = 0; i < jArr.length(); ++i){
                JSONObject jObj = jArr.getJSONObject(i);

                gpsX = jObj.getDouble("gpsX");
                gpsY = jObj.getDouble("gpsY");
                velocity = jObj.getInt("velocity");
                angle = jObj.getDouble("angle");
                roadType = jObj.getString("roadType");

                String sql = "INSERT INTO Articles(gpsX, gpsY, velocity, angle, roadType)"
                        + " VALUES(" + gpsX + ",'" + gpsY + "','" + velocity + "','" + angle + "', '" + roadType + "');";

                try{
                    database.execSQL(sql);
                } catch (Exception e) {
                    Log.e("Dao", "DB ERROR! - " + e);
                    e.printStackTrace();
                }

            }
        } catch (JSONException e){
            Log.e("test", "JSON ERROR! -" + e);
            e.printStackTrace();
        }
    }

    /*
    public ArrayList<Article> getArticleList(){

        ArrayList<Article> articleList = new ArrayList<Article>();

        int articleNumber;
        String title;
        String writer;
        String id;
        String content;
        String writeDate;
        String imgName;

        String sql = "SELECT * FROM Articles;";
        Cursor cursor = database.rawQuery(sql, null);

        while (cursor.moveToNext()){
            articleNumber = cursor.getInt(1);
            title = cursor.getString(2);
            writer = cursor.getString(3);
            id = cursor.getString(4);
            content = cursor.getString(5);
            writeDate = cursor.getString(6);
            imgName = cursor.getString(7);
            articleList.add(new Article(articleNumber, title, writer, id, content, writeDate, imgName));
        }
        cursor.close();
        return articleList;
    }
    */
}
