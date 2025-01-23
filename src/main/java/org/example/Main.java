package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        ArrayList<Student> res = new ArrayList<>();
        Student student = new Student("Anupam", 67);
        System.out.println(student);
        System.out.println(student.getId());
        System.out.println(student.getName());
        res.add(new Student("Ahi", 22));
        System.out.println(res);
        for(Student s: res) {
            String name = s.getName();
            System.out.println(name);
            int id = s.getId();
            System.out.println(id);
        }
        
        System.out.println(Arrays.asList(res));
    }
}