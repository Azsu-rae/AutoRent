package mcp;

import java.util.Vector;

import orm.Table;
import orm.util.Pair;
import orm.util.Reflection;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

class ParsedQuery {

   final private String operation;
   final private String model;

   final private Reflection reflect;
   final private Vector<Table> tuples;
   final private Map<String,Vector<Object>> attributes;
   final private Vector<Pair<Object,Object>> boundedCriterias;

   ParsedQuery(String operation, String model) {

       boolean nil = operation == null || model == null;
       boolean op = Arrays.asList("search", "create", "update", "delete").contains(operation);
       boolean mdl = Table.hasSubClass(model);

       if (nil || !op || !mdl) {
           String s = "Invalid operation: %s or model: %s";
           throw new IllegalArgumentException(String.format(s, operation, model));
       }

       this.operation = operation;
       this.model = model;

       this.reflect = new Reflection(model);
       this.tuples = new Vector<>();
       this.attributes = new HashMap<>();
       this.boundedCriterias = new Vector<>();
   }

   public boolean execute() {

       switch (operation) {
            case "search":
               return search();
            case "create":
               return create();
            case "update":
               return update();
            case "delete":
               return delete();
       }
   }

   public void setBoundedCriteria(String name, Pair<Object,Object> value) {

       if (!value.isValidCriteriaFor(reflect)) {
           String s = "Invalid attribute : %s for model: %s";
           throw new IllegalArgumentException(String.format(s, value.toString(), model));
       }

       boundedCriterias.add(value);
   }

   public void setAttribute(String name, Object value) {

       if (!reflect.fields.type(name).equals(value.getClass())) {
           String s = "Invalid type: %s for attribute: %s of model:  %s";
           throw new IllegalArgumentException(String.format(s, value.getClass().getSimpleName(), name, model));
       }

       attributes.computeIfAbsent(name, k -> new Vector<>()).add(value);
   }

   private Vector<Table> search() {

       Vector<Table> discreteCriterias = new Vector<>();
       for (Map.Entry<String,Vector<Object>> entry : attributes.entrySet()) {

            int count = 1;
           for (Object att : entry.getValue()) {

               if (count > discreteCriterias.size()) {
                    discreteCriterias.add(Reflection.getModelInstance(model));
               }

               discreteCriterias.elementAt(count-1).reflect.setAttribute(entry.getKey(), att);
               count++;
           }
       }

       return Table.search(discreteCriterias, boundedCriterias);
   }

   private boolean create(Table tuple) {

       return true;
   }

   private boolean delete() {

        return true;
   }

   private boolean update() {

       return true;
   }
}
