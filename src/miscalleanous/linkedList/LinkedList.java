package miscalleanous.linkedList;

public class LinkedList {

	public Node first ;
	
	 public void addItem(Node item) {
         if (first == null) { 
             first = item;
         } else if (first != null) { 
             Node current = first;
             while (current.next != null) {
                 current = current.next;
             }
             current.next = item;
         }
     }

     void addFirst(Node item) {
         Node oldFirst = first; 
         item.next = oldFirst;  
         first = item;  
     }
}

