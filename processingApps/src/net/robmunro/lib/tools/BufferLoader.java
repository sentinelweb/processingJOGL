package net.robmunro.lib.tools;

import java.util.ArrayList;
import java.util.HashMap;

import oscP5.OscIn;

public class BufferLoader {
	private ArrayList<Integer> snd_buffer = new ArrayList<Integer>(); 
	private ArrayList<Integer> snd_data  = new ArrayList<Integer>();
	private HashMap<Integer,ArrayList> tmp  = new HashMap<Integer,ArrayList>();
	
	public void process(OscIn oscIn){
		Integer index = oscIn.getInt(0);
		String data = oscIn.getString(1);
		//System.out.println(index+ " --- "+data+":");
		String[] dataSplit=data.split(" ");// was ':'
		ArrayList<Integer> tmp_buf = new ArrayList<Integer>();
		for (int i=0;i<dataSplit.length;i++) {
			try {
				tmp_buf.add(Integer.parseInt(dataSplit[i]));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		tmp.put( index, tmp_buf );
		if (index==0) {
			for (int i=tmp.keySet().size()-1; i>0; i--) {
				if (tmp.get( i )!=null) {
					snd_buffer.addAll(tmp.get( i ));
				}
			}
			snd_data = snd_buffer;
			snd_buffer = new ArrayList<Integer>();
			//System.out.println("rx frame:"+oscIn.getAddrPattern());
		}
	}
	public ArrayList<Integer> getData() {
		return snd_data;
	}
}
