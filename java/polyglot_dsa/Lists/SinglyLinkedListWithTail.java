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
        } else { // if head is not null, it means the list is not empty,
            tail.setNext(newNode); //link the current tail to the new node, e.g [head] -> [node1] -> [tail] -> [newNode] -> null
            tail = newNode; //update the tail reference to the new node, e.g [head] -> [node1] -> [node2] -> [tail/newNode] -> null
        }
        size++; //increment the size of the list by 1
    }
    
}
