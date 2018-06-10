package util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import model.User;

/**
 * Created by Andressa on 07/06/2018.
 */

public class DBController {

    private SQLiteDatabase db;

    public DBController(Context context){
        DBHelper helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insertUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("idUser", u.getIdUser());
        cv.put("name", u.getName());
        cv.put("dateOfBirth", u.getDateOfBirth());
        cv.put("language", u.getLanguage());
        cv.put("occupation", u.getOccupation());
        cv.put("email", u.getEmail());
        cv.put("password", u.getPassword());
        cv.put("localization", u.getLocalization());
        cv.put("statusAccount", u.getStatusAccount().toString());

        db.insert("user", null, cv);
        db.close();
    }

    public void updateUser(User u) {
        ContentValues cv = new ContentValues();
        cv.put("name", u.getName());
        cv.put("dateOfBirth", u.getDateOfBirth());
        cv.put("language", u.getLanguage());
        cv.put("occupation", u.getOccupation());
        cv.put("email", u.getEmail());
        cv.put("password", u.getPassword());
        cv.put("localization", u.getLocalization());
        cv.put("statusAccount", u.getStatusAccount().toString());

        db.update("user", cv, "idUser = ?", new String[]{""+u.getIdUser()});
        db.close();
    }

    public void deleteUser(User u) {
        db.delete("user", "idUser = " + u.getIdUser(), null);
    }

    public User getUser() {
        User u = new User();
        String[] colums = new String[]{"idUser", "name", "dateOfBirth", "language", "occupation", "email", "localization", "statusAccount"};
        //Cursor cursor = db.query("user", colums, "idUser = ?", new String[]{""+u.getIdUser()}, null, null, null);
        Cursor cursor = db.query("user", colums, null, null, null, null, null);

        //db = helper.getReadableDatabase();
        //String sqlGetUser = "SELECT * FROM user";
        //Cursor c = db.rawQuery(sqlGetUser, null);

        if(cursor.getCount() > 0) {
            cursor.moveToNext();

            do {
                u.setIdUser(cursor.getInt(0));
                u.setName(cursor.getString(1));

                String array[];
                array = cursor.getString(2).toString().split("-");
                String aux = array[2]+"/"+array[1]+"/"+array[0];
                u.setDateOfBirth(aux);

                u.setLanguage(cursor.getString(3));
                u.setOccupation(cursor.getString(4));
                u.setEmail(cursor.getString(5));
                u.setPassword(cursor.getString(6));
                u.setLocalization(cursor.getString(7));
                u.setStatusAccount(Enum.valueOf(Status.class, "Active"));
            } while(cursor.moveToNext());
        }

        //db.close();
        return u;
    }
}
