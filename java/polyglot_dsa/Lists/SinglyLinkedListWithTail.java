package java.polyglot_dsa.Lists;

import java.polyglot_dsa.Nodes.SNode;

public class SinglyLinkedListWithTail {
    private SNode head; //reference to the first Node in the list e.g contains 3 nodes [head] -> [node1] -> [node2] -> null
    private SNode tail; //reference to the last Node in the list, used for efficient addition of new nodes at the end of the list
    private int size;   //number of nodes in the list
    //note: size not static because we want to create multiple instances of SinglyLinkedList, each with its own head and size

    public SinglyLinkedListWithTail() {
        this.head = null;   //initially the list is empty, so head is null
        this.tail = null;   //initially the list is empty, so tail is null
        this.size = 0;      //initially the size of the list is 0
    }

    public void add(int value) {
        SNode newNode = new SNode(value); //create a new node with the given value
        if (head == null) { // if head is null, it means the list is empty, so we set both head and tail to the new node
            head = newNode;
            tail = newNode;
            //visually: [head/tail] -> [newNode] -> null
        } else {            // if head is not null, it means the list is not empty,
            tail.setNext(newNode); //link the current tail to the new node, e.g [head] -> [node1] -> [tail] -> [newNode] -> null
            tail = newNode; //update the tail reference to the new node, e.g [head] -> [node1] -> [node2] -> [tail/newNode] -> null
        }
        size++;             //increment the size of the list by 1
    }

    public void remove() {
        if (head == null) { // if head is null, it means the list is empty, so we cannot remove any nodes
            return;         //exit the method without doing anything
        }
        if (head == tail) { // if head is equal to tail, it means the list has only one node
            head = null;    //set head to null, e.g [head/tail] -> null
            tail = null;    //set tail to null, e.g [head/tail] -> null
        } else {            // if head is not equal to tail, it means the list has more than one node
            head = head.getNext(); //update the head reference to the next node, e.g [head] -> [node1] -> [tail] -> null becomes [head/node1] -> [tail] -> null
        }
        size--;             //decrement the size of the list by 1
    }

    

    public void printList() {
        SNode current = head; //start from the head of the list
        while (current != null) { //traverse the list until we reach the end (null)
            System.out.print(current.getValue() + " -> "); //print the value of the current node followed by an arrow
            current = current.getNext(); //move to the next node in the list
        }
        System.out.println("null"); //after printing all nodes, print null to indicate the end of the list
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(); //using StringBuffer for efficient string concatenation
        SNode current = head; //start from the head of the list
        while (current != null) { //traverse the list until we reach the end (null)
            sb.append(current.getValue()).append(" -> "); //append the value of the current node followed by an arrow to the StringBuffer
            current = current.getNext(); //move to the next node in the list
        }
        sb.append("null"); //after appending all nodes, append null to indicate the end of the list
        return sb.toString(); //convert the StringBuffer to a String and return 
    }

    public SNode getHead() {
        return head;
    }

    public SNode getTail() {
        return tail;
    }

    public int getSize() {
        return size;
    }
}
