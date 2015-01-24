package cn.it.download;

import java.util.HashMap;

public class Main {
    
    public static void main(String args[]){
        HashMap<String,A> h;
        h = new HashMap<String, A>();
        A a = new A(1);
        A put = h.put("1", a);
        if(h.get("1") == null){
            System.out.println("haha");
        }
        
    }
    
    
}

class A{
    public int a = 0;
    
    public A(int a){
        this.a = a;
    }
}