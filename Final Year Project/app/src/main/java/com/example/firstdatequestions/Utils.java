package com.example.firstdatequestions;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
this class is used to hold methods that i used multiple times in different activities so i could just call it once from here
 */
public class Utils {

   /*
   method used to get the list of questions from the saved file in internal storage
    */
    public static List<Question> getQuestions(String fileName, Context context){
       try {
           FileInputStream fis = context.openFileInput(fileName);
           ObjectInputStream in = new ObjectInputStream(fis);
           List<Question> list = new ArrayList<>();
           list= (List<Question>) in.readObject();
           in.close();
           return list;
       } catch (IOException ex) {
           ex.printStackTrace();
           return null;
       } catch (ClassNotFoundException ex) {
           ex.printStackTrace();
           return null;
       }

   }

   /*
   used to get display name of the user when navigating across different activites
    */
   public static String getName(Context context){
        try {
            FileInputStream fis = context.openFileInput("displayName");
            ObjectInputStream in = new ObjectInputStream(fis);
            String name = (String) in.readObject();
            in.close();
            return name;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /*
    this method is used to calculate the age of a user using Java's calendar util
    code gotten from https://www.candidjava.com/tutorial/java-program-to-calculate-age-from-date-of-birth/
     */
    public static int userAge(String s){
       try{

           SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
           Date d = sdf.parse(s);
           Calendar c = Calendar.getInstance();
           c.setTime(d);
           int year = c.get(Calendar.YEAR);
           int month = c.get(Calendar.MONTH) + 1;
           int date = c.get(Calendar.DATE);
           LocalDate l1 = LocalDate.of(year, month, date);
           LocalDate now1 = LocalDate.now();
           Period diff1 = Period.between(l1, now1);
           int age = diff1.getYears();
           return age;

       }catch(ParseException e){
           e.printStackTrace();
       }

       return 0;
    }

    /*
    this code is used to get the display size of a sceen to make the matching fragment screen adjustable depending on the size of the phone
    code gotten from https://medium.com/@janishar.ali/modifying-android-tinder-swipe-view-example-to-support-auto-resize-94f9c64f641e.
     */
    public static Point getDisplaySize(WindowManager windowManager){
        try {
            if(Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }else{
                return new Point(0, 0);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Point(0, 0);
        }
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
