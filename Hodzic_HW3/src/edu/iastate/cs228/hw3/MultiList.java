package edu.iastate.cs228.hw3;

import org.w3c.dom.CDATASection;

import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implementation of the list interface based on linked nodes
 * that store multiple items per node.  Rules for adding and removing
 * elements ensure that each node (except possibly the last one)
 * is at least half full.
 */
public class MultiList<E extends Comparable<? super E>> extends AbstractSequentialList<E> {
    /**
     * Default number of elements that may be stored in each node.
     */
    private static final int DEFAULT_NODESIZE = 4;

    /**
     * Number of elements that can be stored in each node.
     */
    private final int nodeSize;

    /**
     * Dummy node for head.  It should be private but set to public here only
     * for grading purpose.  In practice, you should always make the head of a
     * linked list a private instance variable.
     */
    public Node head;

    /**
     * Dummy node for tail.
     */
    private Node tail;

    /**
     * Number of elements in the list.
     */
    private int size;

    /**
     * Constructs an empty list with the default node size.
     */
    public MultiList() {
        this(DEFAULT_NODESIZE);
    }

    /**
     * Constructs an empty list with the given node size.
     *
     * @param nodeSize number of elements that may be stored in each node, must be
     *                 an even number
     */
    public MultiList(int nodeSize) {
        if (nodeSize <= 0 || nodeSize % 2 != 0) throw new IllegalArgumentException();

        // dummy nodes
        head = new Node();
        tail = new Node();
        head.next = tail;
        tail.previous = head;
        this.nodeSize = nodeSize;
    }

    /**
     * Constructor for grading only.  Fully implemented.
     *
     * @param head
     * @param tail
     * @param nodeSize
     * @param size
     */
    public MultiList(Node head, Node tail, int nodeSize, int size) {
        this.head = head;
        this.tail = tail;
        this.nodeSize = nodeSize;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    private class NodeInfo {
        public Node node;
        public int offset;

        public NodeInfo(Node node, int offset) {
            this.node = node;
            this.offset = offset;
        }
    }
    private class eComparator implements Comparator<E>{
        @Override
        public int compare(E left, E right){
            if (left.compareTo(right) > 0){
                return 1;
            }else if (left.compareTo(right) < 0){
                return -1;
            }else{
                return 0;
            }
        }
    }

    public NodeInfo find(int pos) {
        Node temp = head;
        int currentIndex = 0;
        int numOfNodes = 0;
        while (currentIndex <= pos) {
            temp = temp.next;
            currentIndex += temp.count;
            numOfNodes++;
        }
        int totalBefore = 0;
        Node temp2 = head.next;
        while (temp2 != temp) {
            totalBefore += temp2.count;
            temp2 = temp2.next;
        }
        int offset = pos - totalBefore;
        return new NodeInfo(temp, offset);
    }


    @Override
    public boolean add(E item) {
        if (size == 0) {
            Node temp = new Node();
            temp.addItem(item);
            head.next = temp;
            tail.previous = temp;
            temp.previous = head;
            temp.next = tail;
            size++;
            return true;
        } else {
            if (tail.previous.count < DEFAULT_NODESIZE) {
                tail.previous.addItem(item);
                size++;
                return true;
            } else {
                Node temp = new Node();
                temp.addItem(item);
                tail.previous.next = temp;
                temp.previous = tail.previous;
                tail.previous = temp;
                temp.next = tail;
                size++;
                return true;
            }
        }
    }

    public void addHelper(NodeInfo nodeinfo, E item) {
        if (nodeinfo.node.count == DEFAULT_NODESIZE) {
            Node newNode = new Node();
            newNode.addItem(nodeinfo.node.data[2]);
            newNode.addItem(nodeinfo.node.data[3]);
            nodeinfo.node.removeItem(DEFAULT_NODESIZE / 2);
            nodeinfo.node.removeItem(DEFAULT_NODESIZE / 2);
            nodeinfo.node.next.previous = newNode;
            newNode.next = nodeinfo.node.next;
            nodeinfo.node.next = newNode;
            newNode.previous = nodeinfo.node;
            if (nodeinfo.offset <= DEFAULT_NODESI ZE / 2) {
                newNode.previous.addItem(nodeinfo.offset, item);
            } else {
                newNode.addItem((nodeinfo.offset - (DEFAULT_NODESIZE / 2)), item);
            }
        } else {
            nodeinfo.node.addItem(nodeinfo.offset, item);
        }
    }

    @Override
    public void add(int pos, E item) {
        if (size == 0) {
            Node newNode = new Node();
            head.next = newNode;
            newNode.previous = head;
            newNode.next = tail;
            tail.previous = newNode;
            newNode.addItem(item);
            size++;
        } else {
            NodeInfo nodeinfo = find(pos);
            if (nodeinfo.offset == 0) {
                if (nodeinfo.node.previous.count < DEFAULT_NODESIZE && nodeinfo.node.previous != head) {
                    nodeinfo.node.previous.addItem(item);
                } else if (nodeinfo.node == tail) {
                    Node newNode = new Node();
                    nodeinfo.node.previous = newNode;
                    newNode.next = tail;
                    newNode.previous = tail.previous;
                    tail.previous =newNode;
                    newNode.addItem(item);
                }else{
                    addHelper(nodeinfo, item);
                }
            } else {
                addHelper(nodeinfo, item);
            }
            size++;
        }
    }


    @Override
    public E remove(int pos) {
        NodeInfo nodeInfo = find(pos);
        if (nodeInfo.node.next == tail && nodeInfo.node.count == 1) {
            nodeInfo.node.next.previous = nodeInfo.node.previous;
            nodeInfo.node.previous.next = tail;
        } else if (nodeInfo.node.next == tail) {
            nodeInfo.node.removeItem(nodeInfo.offset);
        } else if (nodeInfo.node.count > DEFAULT_NODESIZE / 2) {
            nodeInfo.node.removeItem(nodeInfo.offset);
        } else if (nodeInfo.node.next.count > DEFAULT_NODESIZE / 2) {
            nodeInfo.node.removeItem(nodeInfo.offset);
            nodeInfo.node.addItem(nodeInfo.node.next.data[0]);
            nodeInfo.node.next.removeItem(0);

        } else {
            nodeInfo.node.removeItem(nodeInfo.offset);
            nodeInfo.node.addItem(nodeInfo.node.next.data[0]);
            nodeInfo.node.addItem(nodeInfo.node.next.data[1]);
            nodeInfo.node.next.previous = nodeInfo.node;
            nodeInfo.node.next = nodeInfo.node.next.next;
        }
        size--;
        return null;
    }

    /**
     * Sort all elements in the Multi list in NON-DECREASING order. You may do the following.
     * Traverse the list and copy its elements into an array, deleting every visited node along
     * the way.  Then, sort the array by calling the insertionSort() method.  (Note that sorting
     * efficiency is not a concern for this project.)  Finally, copy all elements from the array
     * back to the Multi list, creating new nodes for storage. After sorting, all nodes but
     * (possibly) the last one must be full of elements.
     * <p>
     * Comparator<E> must have been implemented for calling insertionSort().
     */
    public void sort() {
        Node node = head;
        E[] arr = (E[]) new Comparable[size];
        int currentIndex = 0;
        while(node != tail){
            node = node.next;
            for (int i = 0; i < node.count; i++){
                arr[currentIndex] = node.data[i];
                currentIndex++;
            }
        }
        while(size != 0){
            remove(0);
        }
        insertionSort(arr , new eComparator());
        for (int i = 0; i < arr.length; i++){
            add(arr[i]);
        }

    }

    /**
     * Sort all elements in the Multi list in NON-INCREASING order. Call the bubbleSort()
     * method.  After sorting, all but (possibly) the last nodes must be filled with elements.
     * <p>
     * Comparable<? super E> must be implemented for calling bubbleSort().
     */
    public void sortReverse() {
        Node node = head;
        E[] arr =(E[]) new Comparable[size];
        int currentIndex = 0;
        while(node != tail){
            node = node.next;
            for (int i = 0; i < node.count; i++){
                arr[currentIndex] = node.data[i];
                currentIndex++;
            }
        }
        while(size != 0){
            remove(0);
        }
        bubbleSort(arr);
        for (int i = 0; i < arr.length; i++){
            add(arr[i]);
        }

    }

    @Override
    public Iterator<E> iterator() {
        return new MultiListIterator();
    }

    @Override
    public ListIterator<E> listIterator() {
        return new MultiListIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new MultiListIterator(index);
    }

    /**
     * Returns a string representation of this list showing
     * the internal structure of the nodes.
     */
    public String toStringInternal() {
        return toStringInternal(null);
    }

    /**
     * Returns a string representation of this list showing the internal
     * structure of the nodes and the position of the iterator.
     *
     * @param iter an iterator for this list
     */
    public String toStringInternal(ListIterator<E> iter) {
        int count = 0;
        int position = -1;
        if (iter != null) {
            position = iter.nextIndex();
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Node current = head.next;
        while (current != tail) {
            sb.append('(');
            E data = current.data[0];
            if (data == null) {
                sb.append("-");
            } else {
                if (position == count) {
                    sb.append("| ");
                    position = -1;
                }
                sb.append(data.toString());
                ++count;
            }

            for (int i = 1; i < nodeSize; ++i) {
                sb.append(", ");
                data = current.data[i];
                if (data == null) {
                    sb.append("-");
                } else {
                    if (position == count) {
                        sb.append("| ");
                        position = -1;
                    }
                    sb.append(data.toString());
                    ++count;

                    // iterator at end
                    if (position == size && count == size) {
                        sb.append(" |");
                        position = -1;
                    }
                }
            }
            sb.append(')');
            current = current.next;
            if (current != tail)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Node type for this list.  Each node holds a maximum
     * of nodeSize elements in an array.  Empty slots
     * are null.
     */
    private class Node {
        /**
         * Array of actual data elements.
         */
        // Unchecked warning unavoidable.
        public E[] data = (E[]) new Comparable[nodeSize];

        /**
         * Link to next node.
         */
        public Node next;

        /**
         * Link to previous node;
         */
        public Node previous;

        /**
         * Index of the next available offset in this node, also
         * equal to the number of elements in this node.
         */
        public int count;

        /**
         * Adds an item to this node at the first available offset.
         * Precondition: count < nodeSize
         *
         * @param item element to be added
         */
        void addItem(E item) {
            if (count >= nodeSize) {
                return;
            }
            data[count++] = item;
            //useful for debugging
            //      System.out.println("Added " + item.toString() + " at index " + count + " to node "  + Arrays.toString(data));
        }

        /**
         * Adds an item to this node at the indicated offset, shifting
         * elements to the right as necessary.
         * <p>
         * Precondition: count < nodeSize
         *
         * @param offset array index at which to put the new element
         * @param item   element to be added
         */
        void addItem(int offset, E item) {
            if (count >= nodeSize) {
                return;
            }
            for (int i = count - 1; i >= offset; --i) {
                data[i + 1] = data[i];
            }
            ++count;
            data[offset] = item;
            //useful for debugging
//      System.out.println("Added " + item.toString() + " at index " + offset + " to node: "  + Arrays.toString(data));
        }

        /**
         * Deletes an element from this node at the indicated offset,
         * shifting elements left as necessary.
         * Precondition: 0 <= offset < count
         *
         * @param offset
         */
        void removeItem(int offset) {
            E item = data[offset];
            for (int i = offset + 1; i < nodeSize; ++i) {
                data[i - 1] = data[i];
            }
            data[count - 1] = null;
            --count;
        }
    }


    private class MultiListIterator implements ListIterator<E> {
        // constants you possibly use ...
        public static final int BEHIND = -1;
        public static final int AHEAD = 1;
        public static final int NONE = 0;
        // instance variables ...
        public int index;
        public Node cursor;
        public Node pending;
        public int withinNode;
        public int direction;

        /**
         * Default constructor
         */
        public MultiListIterator() {
            index = 0;
            cursor = find(0).node;
            pending = null;
            withinNode = 0;
        }

        /**
         * Constructor finds node at a given position.
         *
         * @param pos
         */
        public MultiListIterator(int pos) {
            index = pos;
            cursor = find(pos).node;
            pending = null;
            withinNode = find(pos).offset;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                E data = cursor.data[withinNode];
                if (withinNode + 1 > cursor.count - 1) {
                    cursor = cursor.next;
                    withinNode = 0;
                    index++;
                } else {
                    withinNode++;
                    index++;
                }
                direction = BEHIND;
                return data;
            }
        }

        @Override
        public void remove() {
            if (direction == NONE){
                throw new IllegalStateException();
            }else if (direction == AHEAD){
                NodeInfo nodeInfo = find(index);
                if (nodeInfo.node.next == tail && nodeInfo.node.count == 1) {
                    nodeInfo.node.next.previous = nodeInfo.node.previous;
                    nodeInfo.node.previous.next = tail;
                } else if (nodeInfo.node.next == tail) {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                } else if (nodeInfo.node.count > DEFAULT_NODESIZE / 2) {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                } else if (nodeInfo.node.next.count > DEFAULT_NODESIZE / 2) {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                    nodeInfo.node.addItem(nodeInfo.node.next.data[0]);
                    nodeInfo.node.next.removeItem(0);

                } else {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                    nodeInfo.node.addItem(nodeInfo.node.next.data[0]);
                    nodeInfo.node.addItem(nodeInfo.node.next.data[1]);
                    nodeInfo.node.next.previous = nodeInfo.node;
                    nodeInfo.node.next = nodeInfo.node.next.next;
                }
                cursor = find(index).node;
            }else{
                NodeInfo nodeInfo = find(index-1);
                if (nodeInfo.node.next == tail && nodeInfo.node.count == 1) {
                    nodeInfo.node.next.previous = nodeInfo.node.previous;
                    nodeInfo.node.previous.next = tail;
                } else if (nodeInfo.node.next == tail) {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                } else if (nodeInfo.node.count > DEFAULT_NODESIZE / 2) {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                } else if (nodeInfo.node.next.count > DEFAULT_NODESIZE / 2) {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                    nodeInfo.node.addItem(nodeInfo.node.next.data[0]);
                    nodeInfo.node.next.removeItem(0);

                } else {
                    nodeInfo.node.removeItem(nodeInfo.offset);
                    nodeInfo.node.addItem(nodeInfo.node.next.data[0]);
                    nodeInfo.node.addItem(nodeInfo.node.next.data[1]);
                    nodeInfo.node.next.previous = nodeInfo.node;
                    nodeInfo.node.next = nodeInfo.node.next.next;
                }
                index--;
                cursor = find(index).node;
            }
            direction = NONE;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            } else {
                E data;
                if (withinNode - 1 < 0) {
                    cursor = cursor.previous;
                    withinNode = cursor.count - 1;
                    data = cursor.data[withinNode];
                    index--;
                } else {
                    withinNode--;
                    data = cursor.data[withinNode];
                    index--;
                }
                direction = AHEAD;
                return data;
            }
        }

        @Override
        public int nextIndex() {
            return index;

        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void set(E item) {
            if (direction == NONE) {
                throw new IllegalStateException();
            } else if (direction == BEHIND) {
                if (withinNode - 1 < 0) {
                    cursor.previous.data[cursor.count - 1] = item;
                } else {
                    cursor.data[withinNode - 1] = item;
                }
            } else {
                if (withinNode > cursor.count - 1) {
                    cursor.next.data[0] = item;
                } else {
                    cursor.data[withinNode] = item;
                }
            }

        }

        @Override
        public void add(E item) {
            NodeInfo nodeinfo = find(index);
            if (nodeinfo.offset == 0) {
                if (nodeinfo.node.previous.count < DEFAULT_NODESIZE && nodeinfo.node.previous != head) {
                    nodeinfo.node.previous.addItem(item);
                } else if (nodeinfo.node == tail) {
                    Node newNode = new Node();
                    nodeinfo.node.previous = newNode;
                    newNode.next = tail;
                    newNode.previous = tail.previous;
                    tail.previous =newNode;
                    newNode.addItem(item);
                }else{
                    addHelper(nodeinfo, item);
                }
            } else {
                addHelper(nodeinfo, item);
            }
            index++;
            cursor = find(index).node;
            size++;
        }
        // Other methods you may want to add or override that could possibly facilitate
        // other operations, for instance, addition, access to the previous element, etc.
        //
        // ...
        //
    }


    /**
     * Sort an array arr[] using the insertion sort algorithm in the NON-DECREASING order.
     *
     * @param arr  array storing elements from the list
     * @param comp comparator used in sorting
     */
    private void insertionSort(E[] arr, Comparator<? super E> comp) {
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            E curr = arr[i];
            int j = i - 1;
            while (j >= 0 && comp.compare(arr[j] , curr) > 0) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = curr;
        }
    }

    /**
     * Sort arr[] using the bubble sort algorithm in the NON-INCREASING order. For a
     * description of bubble sort please refer to Section 6.1 in the project description.
     * You must use the compareTo() method from an implementation of the Comparable
     * interface by the class E or ? super E.
     *
     * @param arr array holding elements from the list
     */
    private void bubbleSort(E[] arr) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) < 0) {
                    E temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }


}