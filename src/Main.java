import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;


class Condition
{
    String field,value,operator;

    Condition(String field,String operator,String value )
    {
        this.field=field;
        this.operator=operator;
        this.value=value;

    }

    boolean checkOperation(String val1,String val2)
    {
        boolean flag=false;
    switch (this.operator) {
        case "#gt":
            flag= val1.compareTo(val2) >0 ?true:false;
            break;
        case "#ls":
            flag= val1.compareTo(val2)<0;
            break;
        case "#eq":
            flag= val1.compareTo(val2)==0;
            break;

        }
        return flag;
}

}



class Query
{
    String type;
    ArrayList<Object> and;
    Condition c;
    Query(Condition c,String type)
    {
        this.type=type;
        this.c=c;
    }
    Query(ArrayList<Object> and,String type)
    {
        this.type=type;
        this.and=and;
    }


}

/**
 * This class allow to perform CRUD operation on Nosql database
 * When you create an object a json file is created with specified name,
 * if name already exists it does not create an object throws execption
 */



class MyMongoDb
{
    File file;
    MyMongoDb(String fname) throws IOException {
         this.file = new File("./"+fname+".json");
        if (file.createNewFile()) {

            System.out.println("File " + file.getName() + " is created successfully.");

        } else {
           // throw new IOException("File is already exists");
        }
    }

    /**
     * Creates a filed in file
     * @param obj
     * @return true if value is inserted false if failed
     */
    public boolean insert(JSONArray obj)
    {
        JSONArray arr=find();
        for(int i=0;i<obj.size(); i++)
        {
            JSONObject b=(JSONObject)obj.get(i);
            b.put("_id",String.valueOf(Main.Generate()));
            arr.add(b);
        }

            try (FileWriter file = new FileWriter(this.file)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(arr.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

            return true;
    }

    /**
     *
     * @return null if there is no contents in file else return all the elements in the file
     */
    public JSONArray find()
    {
        JSONParser parser = new JSONParser();
        JSONArray arr=new JSONArray();
        Object obj = null;

        try {
            obj = parser.parse(new FileReader(this.file));
             arr=(JSONArray) obj ;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            obj=new Object();
            System.out.println("Empty");
        }
        return arr;
    }
    public JSONArray find(Query query)
    {
        JSONArray arr=find();
        JSONArray ans=new JSONArray();
        for(int i=0;i<arr.size(); i++)
        {
            JSONObject b=(JSONObject)arr.get(i);

            if(query.type=="Condition")
            {
                if(query.c.checkOperation(String.valueOf(b.get(query.c.field)),query.c.value))
                    ans.add(b);
            }
            else if(query.type=="And")
            {
                boolean flag=true;

                for(int j=0;j<query.and.size();j++)
                {
                    Condition c=(Condition)query.and.get(j);
                    flag&=c.checkOperation(String.valueOf(b.get(c.field)),c.value);

                    if(!flag) break;
                }

                if(flag) ans.add(b);

            }




        }


        return ans;
    }



//    public boolean update(JSONObject query,JSONObject obj)
//    {
//        ArrayList<String> keys = (ArrayString<String>) query.keyset();
//
//        JSONArray arr=find();
//     return true;
//    }

    /**
     *
     * @return true if all the elements are deleted
     */
    public boolean remove()
    {
        try (FileWriter file = new FileWriter(this.file)) {
            file.write("");
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}


public class Main {

        public static UUID Generate() {
            UUID uuid = UUID.randomUUID();
            return uuid;
        }


    public static void main(String[] args) {
        MyMongoDb m1=null;
        try{
             m1=new MyMongoDb("simple3");
        }
        catch (IOException e) {
            System.out.println(e);
        }





        JSONObject employeeDetails = new JSONObject();
        employeeDetails.put("_id", "1");
        employeeDetails.put("firstName", "Lokesdfsdfdfsh");
        employeeDetails.put("lastName", "Gupta");
        employeeDetails.put("website", "howtodoinjava.com");

        JSONObject employeeDetails2 = new JSONObject();
        employeeDetails2.put("_id", "2");
        employeeDetails2.put("firstName", "Lokesdfsasdasdasddfdfsh");
        employeeDetails2.put("lastName", "Guasddpta");
        employeeDetails2.put("website", "howtasdasdodoinjava.com");

        JSONArray employeeList = new JSONArray();
        employeeList.add(employeeDetails);
        employeeList.add(employeeDetails2);


        //inset

        //System.out.println(m1.insert(employeeList));

        //read
       // System.out.println(m1.find());
//        Query qry=new Query();

        Condition c=new Condition("lastName","#eq","Guasddpta");
        Condition c2=new Condition("_id","#eq","2");

        ArrayList<Object> and=new ArrayList<>();
        and.add(c);
        and.add(c2);
        Query q=new Query(c2,"Condition");

        System.out.println(m1.find(q));


//        JSONObject query=new JSONObject();
//        query.put("_id","2");
//        JSONObject ob=new JSONObject();
//        ob.put("firstName","test");
//        System.out.println(m1.update());

//        System.out.println(m1.remove());
        System.out.println("Hello world! "+ m1 );
    }
}