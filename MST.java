import java.util.Arrays;

/**
 * Created by yinningliu on 3/19/17.
 */
public class MST {

    //Number of vertices in the tree
    private int V;

    // construct MST
    public MST(int size){
        this.V = size;
    }


    // get min key by using heap
    private HeapElement minKey(MinHeap heap, boolean[] mstSet){
        int min_index = 0;
        HeapElement he =null;
        // if heap is not empty
        while(!heap.isEmpty()){

            he = heap.deleteMin();
            // get min key
            min_index = he.getKey();
            // if key has not been used, stop. Otherwise, keep going.
            if(mstSet[min_index]==false) {
                break;
            }
        }

        return he;
    }


    // this primeMST does not require a key[] to save shortest distance.
    // you may count this one as my scores. PrimMST is my optional method.
    public int[] primMST2(double graph[][]){

        // define return parent array
        int[] parent = new int[V];

        // define a heap
        MinHeap heap =  new MinHeap(V);

        // create a check list(if visited)
        boolean[] mstSet = new boolean[V];


        // define each visited is false
        for(int i = 0;i<V;i++){
            mstSet[i] = false;
        }

        // add first vertex into heap
        heap.add(0,0,0);

        // we shall do V steps from each vertex
        for(int count = 0;count< V;count++){

            // get next smallest vertex
            HeapElement he = minKey(heap,mstSet);

            // get index
            int u = he.getKey();

            // set up that we have visited this vertex
            mstSet[u] = true;

            // we have visited this vertex, thus, we may save its parent index into parent array
            parent[u] = he.getParent();

            // update distance for each vertex
            for(int v = 0;v<V;v++){

                // if it is not visited and it is not itself, add into the heap
                if(graph[u][v]!=0 && mstSet[v] == false){
                    // add new smaller key,distance,parent into heap
                    heap.add(v,graph[u][v],u);
                }

            }
        }

        // return parent array
        return parent;
    }

    // this primeMST require a key[] to save shortest distance.
    public int[] primMST(double graph[][]){

        // initialized parent array
        int parent[] = new int[V];

        // create a smallest distance
        double key[] = new double[V];

        // create a new heap
        MinHeap heap =  new MinHeap(V);

        // create a check list(if visited)
        boolean[] mstSet = new boolean[V];


        // set up each distance values(Max) and each visited is false
        for(int i = 0;i<V;i++){
            key[i] = Integer.MAX_VALUE;
            mstSet[i] = false;
        }

        // initialize first step
        key[0] = 0;
        heap.add(0,0);
        parent[0] = 0;

        // we shall do V steps from each vertex
        for(int count = 0;count< V;count++){

            // get next smallest vertex
            int u = minKey(heap,mstSet).getKey();

            // set up that we have visited this vertex
            mstSet[u] = true;

            // update distance for each vertex
            for(int v = 0;v<V;v++){

                // if it is not visited, new distance is smaller than current distance
                if(graph[u][v]!=0 && mstSet[v] == false && graph[u][v] < key[v]){
                    // set up its parent
                    parent[v] = u;
                    // add new smaller key,distance into heap
                    heap.add(v,graph[u][v]);
                    // save this new smaller distance for certain vertex
                    key[v] = graph[u][v];
                }

            }
        }

        // return parent array to calculate TSP
        return parent;
    }

    // calculate TSP by using preOrderTreeWalk (DFS)
    public SinglyLinkedList preOrderTreeWalk(int[] parent){
            // set up a return list
            SinglyLinkedList res = new SinglyLinkedList();

            // i used this list like stack
            SinglyLinkedList linkedList = new SinglyLinkedList();

            // set up visited array to check whether we have visited vertex
            boolean[] visited = new boolean[V];
            // push root
            linkedList.addCharAtBegin(0);

            // push other root, because TSP will be back to root
            linkedList.addCharAtBegin(0);

            // set up 0 is visited
            visited[0] = true;
            while(!linkedList.isEmpty()){

                // get last vertex(key)
                int cur = linkedList.removeCharAtBegin();

                for(int j=V-1;j>0;j--){
                    // if parent is equal to last vertex and we have visited that vertex, push into the stack
                    if(parent[j]==cur && visited[j]==false){
                        // push into the stack
                        linkedList.addCharAtBegin(j);
                        // we have visited this vertex, set up visited to true
                        visited[j] = true;
                    }
                }
                // add key into return linkedlist
                res.addCharAtEnd(cur);
            }
            return res;
    }


}
