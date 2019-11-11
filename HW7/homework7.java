import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;
import java.util.Scanner;
/*
    TO-DO:
        finish Role-Object Maps
        make role sets
        test/run
*/

public class homework7{
    private Dictionary roles_hierarchy; // roles_hierarchy[Role][0-Parent else-Children] 
    private File roles; // roles hierarchy file
    private Map ROMatrix; // Permission matrix
    private File role_object; // permission matrix file
    private File role_sets; // SSD

    private homework7(){
        roles = new File("roleHierarchy.txt");
        role_object = new File("");
    }

    private boolean validate(){
        if (roles_hierarchy.size() == 0) return false;
        Enum roles_Enum = roles_hierarchy.keys();
        int counter = 0;
        for (String key : roles_Enum) {
            ArrayList temp = roles_hierarchy.get(key);
            if(temp.get(0).equals("None")){
                counter++;
            }
        }
        if(counter == 0) return false;
        if(counter > 1) return false;
        return true;
    }
    
    public boolean pull_roles(){
        // try-catch file-in "roleHierarchy.txt"
        // <ascendent looks up> <descendent looks down>
        // if array[role][0] not-null and needs update - return false
        try (Scanner in = new Scanner(roles)){
            while(in.hasNext()){
                String ascendent = in.next();
                String descendent = in.next();
                if (roles_hierarchy.get(descendent) == null) {
                    ArrayList adjacency = new ArrayList<String>();
                    adjacency.add("None");
                    adjacency.add(descendent);
                    roles_hierarchy.put(ascendent, adjacency);
                } else if (roles_hierarchy.get(descendent) != null) {
                    ArrayList temp = roles_hierarchy.get(descendent);
                    temp.add(ascendent);
                }
                ArrayList temp2 = new ArrayList<String>();
                temp2.add(descendent);
                ArrayList null_if_valid = roles_hierarchy.put(ascendent, temp2);
                if (null_if_valid != null){
                    return false;
                }
            }
        }
        if !validate() return false;
        return true;
    }
    //Too tired, need to finish role objecy maps
    private boolean pull_ROMatrix(){
        Enum all_roles = roles_hierarchy.keys();
        ROMatrix = new Map<String, Map>();
        for (String role : all_roles) {
            ROMatrix.put(role, new Map<String, String>())
        }
    }

    public static void main(String[] args) {
        runner = new homework7();
        
        while(!pull_roles()){
            System.out.println("This file is invalid. Hit Enter to continue after verifying file.");
            try{System.in.read()} catch(Exception e){}
        }

        pull_ROMatrix();

    }


}