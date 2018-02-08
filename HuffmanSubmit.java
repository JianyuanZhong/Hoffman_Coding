import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

// Import any package as required


public class HuffmanSubmit implements Huffman {

	// Feel free to add more methods and variables as required. 

	HashMap<Character,Integer> map=new HashMap<Character, Integer>();
	HashMap<Character,ArrayList<Boolean>> codeSet=new HashMap<>();


	public void mapCode(BTNode root,ArrayList<Boolean> code){
		ArrayList<Boolean> realCode=new ArrayList<>();
		for(int i=0;i<code.size();i++) realCode.add(code.get(i));
		if(root!=null){
			if(root.isleave){
				realCode.add(root.code);
				realCode.remove(0);
				codeSet.put(root.c, realCode);
			}else{
				realCode.add(root.code);
				mapCode(root.left,realCode);
				mapCode(root.right,realCode);
			}
		}
	}


	public BTNode buidTree(Heap hp, Heap rq){
		rq=new Heap();

		//for(BTNode i: hp.tree) System.out.print(i.c+"/"+i.freq+" "); 
		while(hp.size>1){
			BTNode parent=new BTNode(0);
			parent.left=hp.poll();
			parent.right=hp.poll();
			parent.freq=parent.left.freq+parent.right.freq;
			rq.offer(parent);
			//for(BTNode i: hp.tree) System.out.print(i.c+"/"+i.freq+" "); 
		}

		if(hp.size>0) 
			rq.offer(hp.poll());


		while(rq.size>1){
			BTNode parent=new BTNode(0);
			parent.left=rq.poll();
			parent.right=rq.poll();
			parent.freq=parent.left.freq+parent.right.freq;
			parent.left.parent=parent;
			parent.right.parent=parent;
			rq.offer(parent);

		}

		new BTNode(0).giveCode(rq.peek(), true);
		return rq.peek();

	}

	public void encode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here		
		BinaryIn in=new BinaryIn(inputFile);
		while(!in.isEmpty()){
			char c=in.readChar();
			if(map.get(c)==null) map.put( c, 1);
			else map.put((char)c, map.get( c)+1);
		}
		
		//Unicode for BLACK BOX, Add it for the EOF_char
		map.put('\u25A0', 1);
		
		//write the freqFile
		BufferedWriter writer=null;
		try {
			writer=new BufferedWriter(new FileWriter(new File(freqFile)));
			for(Map.Entry<Character, Integer> m: map.entrySet()){
				String str=Integer.toBinaryString(m.getKey());
				while(str.length()<8) str="0"+str;
				writer.write(str+":"+m.getValue()+"\n");
				
			}
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		Heap hp=new Heap();
		for(Map.Entry<Character, Integer> m: map.entrySet()){
			BTNode leaf=new BTNode(m.getKey(),m.getValue());
			leaf.isleave=true;
			hp.offer(leaf);
		}
		
		BTNode root=buidTree(hp,new Heap());
		//root.level_order_print(root);
		
		mapCode(root,new ArrayList<Boolean>());
		
		in=new BinaryIn(inputFile);
		BinaryOut out=new BinaryOut(outputFile);
		while(!in.isEmpty()){
			char c=in.readChar();
			ArrayList<Boolean> code=codeSet.get(c);
			//System.out.print(c);
			
			for(Boolean i: code){
				out.write(i);
			//	System.out.print(i);
			}
			
			
		}
		//Write EOF_char
		ArrayList<Boolean> code=codeSet.get('\u25A0');
		for(Boolean i: code) out.write(i);
		
		out.flush();
//		out.close();

		


	}


	public void decode(String inputFile, String outputFile, String freqFile){		
		BufferedReader reader=null;
		BufferedReader checker=null;
		try{
			reader=new BufferedReader(new FileReader(new File(freqFile)));
			String line=reader.readLine();
			Heap hp=new Heap();
			while(line != null){
				String[] element=line.split(":");
				BTNode leaf;
				leaf=new BTNode(element[0], Integer.parseInt(element[1]));
				leaf.isleave=true;
				hp.offer(leaf);
				line=reader.readLine();
			}
				
			
			BTNode root=buidTree(hp,new Heap());
			root.level_order_print(root);
			
			BinaryIn in=new BinaryIn(inputFile);
			BinaryOut out=new BinaryOut(outputFile);
			BTNode pointer=root;
			while(!in.isEmpty()){
				
				if(pointer.isleave) {
					// write char in the leave, once encountered the EFO break!
					try{
						out.write(pointer.c,8);
					}catch(IllegalArgumentException e){
						break;
					}
					pointer=root;
				}else if(in.readBoolean())
					pointer=pointer.left;
				else
					pointer=pointer.right;
				
			}
			
			out.flush();
			out.close();
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}




	public static void main(String[] args) throws FileNotFoundException{
		Huffman  huffman = new HuffmanSubmit();
		huffman.encode("me.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "me_dec.jpg", "freq.txt");
		// After decoding, both ur.jpg and ur_dec.jpg should be the same. 
		// On linux and mac, you can use `diff' command to check if they are the same. 
	}
	public class Heap {

		BTNode [] tree=new BTNode[1000];
		int size=0;
		public BTNode peek(){
			return tree[0];
		}

		public void offer(BTNode data){
			tree[size]=data;
			bubbleUp(size);
			size++;
		}

		public BTNode poll(){
			BTNode temp= tree[0];
			tree[0]=tree[size-1];
			tree[size-1]=null;
			size--;
			tickleDown(0);
			return temp;
		}

		public void bubbleUp(int pos){
			if(pos>0 && tree[pos].freq<tree[(pos-1)/2].freq){
				BTNode temp=tree[pos];
				tree[pos]=tree[(pos-1)/2];
				tree[(pos-1)/2]=temp;
				bubbleUp((pos-1)/2);
			}
		}

		public void tickleDown(int pos){
			int left=2*pos+1;
			int right=left+1;
			int greaterChild;
			if(left<size) {
				greaterChild=left;

				if(right<size&&tree[left].freq>tree[right].freq){
					greaterChild=right;
				}
				if(tree[greaterChild].freq<tree[pos].freq){
					BTNode temp=tree[greaterChild];
					tree[greaterChild]=tree[pos];
					tree[pos]=temp;
					tickleDown(greaterChild);
				}
			}
		}

	}

	public class BTNode {
		char c=(char)-1;
		int num_bits;
		int freq;
		boolean isleave;
		boolean code;
		BTNode left;
		BTNode right;
		BTNode parent;



		public BTNode(String c,int freq){
			this.c= (char) Integer.parseInt(c, 2);
			this.num_bits = c.length();
			this.freq=freq;

		}

		public BTNode(char c,int freq){
			this.c= c;
			this.num_bits = -1;
			this.freq=freq;

		}

		public BTNode(int freq){
			this.freq=freq;
		}

		public void printLevel(BTNode root,int level){
			if(root==null) return;
			if(level==0){
				System.out.print("("+(char)root.c+","+root.code+") ");
			}

			printLevel(root.left,level-1);
			printLevel(root.right,level-1);
		}

		public int getHeight(BTNode root){
			if(root==null) return -1;

			int left=getHeight(root.left);
			int right=getHeight(root.right);

			if(left>right) return left=left+1;
			else return right=right+1;
		}

		public void level_order_print(BTNode root){
			int height=getHeight(root);
			for(int i=0;i<=height;i++){
				printLevel(root,i);
				System.out.println();
			}


		}

		public void giveCode(BTNode root, Boolean code){
			if(root!=null){
				root.code=code;
				giveCode(root.left,true);
				giveCode(root.right,false);
			}
		}


	}
}
