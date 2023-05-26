import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Client {

     static int fifo_pf = 0;//page faults
     static int lru_pf = 0;
     static int SC_pf = 0;

    public static void main(String[] args) {
        try{
            Socket s=new Socket("localhost",8080);
            DataInputStream dis=new DataInputStream(s.getInputStream());
            int n = dis.readInt();
            Queue<Integer> buffer = new LinkedList<Integer>();

            Stack<Integer> stk= new Stack<>(); // lru
            Queuef fifoQ = new Queuef(n); //fifo
            BitQueue SCQ = new BitQueue(n); //second-chance

            int cnumber = dis.readInt();
            while (cnumber != 0){
                 buffer.add(cnumber);
                 cnumber = dis.readInt();
            }
            System.out.println("page trace:");
            for (Integer input: buffer) {
                System.out.print(input + ",");
            }
            System.out.println();
            for (Integer item: buffer) {
                System.out.println("new item is:"+item);
                //fifo
                if(fifoQ.isIn(item)){

                } else {
                    ++fifo_pf;
                    fifoQ=fifo(fifoQ,item);
                }

                //lru
                if(stk.contains(item)){
                    stk.remove(item);
                    stk.push(item);
                }else{
                    ++lru_pf;
                    if(stk.size() < n){
                        stk.push(item);
                    }else {
                        stk.remove(stk.firstElement());
                        stk.push(item);
                    }
                   // lru();
                }

                //second-chance
                int index_isin = SCQ.isIn(item);//return -1 if isnt in & return index of element if it's in
                if(index_isin != -1){
                    SCQ.queue[index_isin].bit =1;
                }else{
                    ++SC_pf;
                    SCQ=second_chance(SCQ,item);
                }

                System.out.println("Customers at the table are:");
                System.out.println("FIFO:");
                fifoQ.queuedisplay();
                System.out.println();
                System.out.println("LRU:");
                PrintStack(stk);
                System.out.println();
                System.out.println("Second-chance:");
                SCQ.queuedisplay();
                System.out.println();
            }

            System.out.println("LRU:"+lru_pf+",FIFO:"+fifo_pf+",Second-chance:"+SC_pf);

            dis.close();
            s.close();
        }catch(Exception e){System.out.println(e);}
    }

    static Queuef fifo(Queuef q,int num){
        if (q.cap != q.rear) { //not full
            q.queueenq(num);
        }else{
            q.queuedeq();
            q.queueenq(num);
        }
     return q;
    }

    //static void lru(){

   // }
    static BitQueue second_chance(BitQueue bitQueue,int num){
        IntBit newIntBit = new IntBit(num);
        if (bitQueue.cap != bitQueue.rear) { //not full
            bitQueue.queueenq(newIntBit);
        }else {
            for (int i = bitQueue.front; i < bitQueue.rear; i++) {
                if (bitQueue.queue[i].bit == 0) {
                    bitQueue.queuedeq();
                    bitQueue.queueenq(newIntBit);
                    break;
                } else {//bit==1
                    IntBit temp = new IntBit(bitQueue.queue[i].value);
                    bitQueue.queuedeq();
                    bitQueue.queueenq(temp);
                }
            }
        }
      return bitQueue;
    }

    public static void PrintStack(Stack<Integer> s)
    {
        Stack<Integer> temp = new Stack<Integer>();

        while (s.empty() == false)
        {
            temp.push(s.peek());
            s.pop();
        }

        while (temp.empty() == false)
        {
            int t = temp.peek();
            System.out.print(t + " ");
            temp.pop();

            // To restore contents of
            // the original stack.
            s.push(t);
        }
    }
}



class Queuef {
     int front, rear, cap;
     int queue[];

    Queuef(int c)
    {
        front = rear = 0;
        cap = c;
        queue = new int[cap];
    }


    void queueenq(int data)
    {
        // check queue is full or not
        if (cap == rear) {
            System.out.printf("\nQueue is full\n");
            return;
        }

        // insert element at the rear
        else {
            queue[rear] = data;
            rear++;
        }
        return;
    }

    // function to delete an element
    // from the front of the queue
    void queuedeq()
    {

        if (front == rear) {
            System.out.printf("\nQueue is empty\n");
            return;
        }
        else {
            for (int i = 0; i < rear - 1; i++) {
                queue[i] = queue[i + 1];
            }
            if (rear < cap)
                queue[rear] = 0;
            rear--;
        }
        return;
    }
    void queuedisplay()
    {
        if (front == rear) {
            System.out.printf("\nQueue is Empty\n");
            return;
        }
        for (int i = front; i < rear; i++) {
            System.out.print(queue[i]+ " ");
        }
        return;
    }
     void queuefront()
    {
        if (front == rear) {
            System.out.printf("\nQueue is Empty\n");
            return;
        }
        System.out.printf("\nFront Element is: %d", queue[front]);
        return;
    }

    boolean isIn(int num){
        for (int i = front; i < rear; i++) {
            if (num==queue[i])
                return true;
        }
        return false;
    }
}


class IntBit{
    int value;
    int bit;

    IntBit(int v){
        this.value=v;
        this.bit = 0;
    }
}

class BitQueue {
     int front, rear, cap;
     IntBit queue[];

    BitQueue(int c)
    {
        front = rear = 0;
        cap = c;
        queue = new IntBit[cap];
    }


    void queueenq(IntBit data)
    {
        // check queue is full or not
        if (cap == rear) {
            System.out.printf("\nQueue is full\n");
            return;
        }

        // insert element at the rear
        else {
            queue[rear] = data;
            rear++;
        }
        return;
    }

    // function to delete an element
    // from the front of the queue
    void queuedeq()
    {

        if (front == rear) {
            System.out.printf("\nQueue is empty\n");
            return;
        }
        else {
            for (int i = 0; i < rear - 1; i++) {
                queue[i] = queue[i + 1];
            }
            if (rear < cap)
                queue[rear] = new IntBit(0);
            rear--;
        }
        return;
    }
     void queuedisplay()
    {
        if (front == rear) {
            System.out.printf("\nQueue is Empty\n");
            return;
        }
        for (int i = front; i < rear; i++) {
            System.out.print(queue[i].value + " ");
        }
        return;
    }
    void queuefront()
    {
        if (front == rear) {
            System.out.printf("\nQueue is Empty\n");
            return;
        }
        System.out.printf("\nFront Element is: %d", queue[front].value);
        return;
    }
    int isIn(int num){
        for (int i = front; i < rear; i++) {
            if (num==queue[i].value)
                return i;
        }
        return -1;
    }
}