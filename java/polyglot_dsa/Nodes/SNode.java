package java.polyglot_dsa.Nodes;


//* Singly Linked Node, contains a value and a reference to the next node in the list.*/
public class SNode {
    private int value;  //using int for simplicity, but new SGNode will be created for generic types in the future
    private SNode next; //note: this is a reference to the next node in the list, not a pointer like in C/C++
    
    public SNode(int value) {
        this.value = value;
        this.next = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public SNode getNext() {
        return next;
    }

    public void setNext(SNode next) {   // setNext is used to link nodes together, e.g node1.setNext(node2) will link node1 to node2
        this.next = next;
    }
}
