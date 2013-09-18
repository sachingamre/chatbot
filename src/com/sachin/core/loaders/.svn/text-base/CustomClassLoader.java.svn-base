/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sachin.core.loaders;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Custom class loader. Loads a given class from the specified location or from system
 *
 *  @author sachin
 */
public class CustomClassLoader extends ClassLoader {
    
    public static CustomClassLoader cclInstance = null ;

    private CustomClassLoader(){
        super(CustomClassLoader.class.getClassLoader());
    }

    public static CustomClassLoader getInstance() {
        if(cclInstance == null) {
            cclInstance = new CustomClassLoader() ;
        }
        return cclInstance ;
    }

    @Override
    public Class loadClass(String className) {
         return findClass(className);
    }

    @Override
    public Class findClass(String className) {
        byte classByte[];
        Class result=null;
        result = (Class)classes.get(className);
        if(result != null){
            return result;
        }

        try {
            return findSystemClass(className);
        }
        catch(Exception ex) {
            Logger.getLogger(CustomClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        try{
           String classPath =    ((String)ClassLoader.getSystemResource(className.replace('.',File.separatorChar)+".class").getFile()).substring(1);
           classByte = loadClassData(classPath);
            result = defineClass(className,classByte,0,classByte.length,null);
            classes.put(className,result);
            return result;
        }
        catch(Exception ex){
            Logger.getLogger(CustomClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null ;
        }
    }

    private byte[] loadClassData(String className) throws IOException{

        File f ;
        f = new File(className);
        int size = (int)f.length();
        byte buff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        return buff;
    }

    private HashMap classes = new HashMap();
}
