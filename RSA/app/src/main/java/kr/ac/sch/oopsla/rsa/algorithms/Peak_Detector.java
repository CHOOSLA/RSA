package kr.ac.sch.oopsla.rsa.algorithms;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class Peak_Detector {
	private static String TAG = "PEAK_DETECTION";
	
	public Peak_Detector()
	{
		
	}
	
	public static double getHR(double[] set, double fs)
	{
		double HR = 0;
		int[] peaks = Peak_Detection(set,fs);
		Log.e("fs",String.valueOf(fs));
		double peak_detect = 0;
		
		if(peaks.length <= 1)
		{
			return 0;
		}
		
		for(int i = 0; i < peaks.length - 1; i++)
		{
			HR = HR + peaks[i+1] - peaks[i];
			peak_detect++;
		}
		HR = HR / peak_detect;
		//Write method for  calculated HR;
		
		return HR;
	}
	
	//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Peak Detect %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	public static int[] Peak_Detection(double[] set, double fs)
	{
		Log.e(TAG, "peak detection fs : "+fs);
		int counter = 0;
		//Extra variable for initialization
		double TH;
		
		//Does initial Setup of data
		double[] sig_all = new double[set.length];
		for(int i=0; i<set.length;i++)
			sig_all[i] = set[i];
		
		int Lx = sig_all.length;
		double[] sig_temp = Arrays.copyOf(sig_all, Lx);
		double[] final_result = new double[Lx];
		
		// set == sig_all == sig_temp
		
		int sh = (int) fs;
		int w = (int) (fs*2);
		
		double[] pc_G_pre = new double[0];
		double TH_maxtomedian = 1000;
		
		int i= 1;
		int j =1;
		
		double[] sig = new double[w];
		int j_start;
		
		double[] x11,x12,x21,x22,x23,p2;
		
		ArrayList<Object> result;
		while(i<Lx-w)
		{
			// 없어도 되지 않나?
			// sig = new double[w];
			System.arraycopy(sig_all, i-1, sig, 0, w);
			TH = TH_maxtomedian * 3;
			
			//eliminate_DCoff
			// result: ArrayList<Object>
			//	- new_sig: Array of double
			//	- TH_maxtomedian: Double
			//	- j_start: Integer
			result = eliminate_DCoff(sig,TH);
			sig = (double[]) result.get(0);
			TH_maxtomedian = (Double) result.get(1);
			j_start = (Integer) result.get(2);
			
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//%%%%% Band pass, Derivatives & Find Max %%%%%%%%%%%%%%
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			x11 = lowpass_principal(sig,fs,5);
			x12 = highpass_filter(x11,0.5);	
			
			int Nfft = 256;
			double[] p,f;
			double val,h;
			int ind;
			ArrayList<double []> psd_result = psd_welch(x12,x12.length,fs,Nfft);
			p = psd_result.get(0);
			f = psd_result.get(1);
			ind = maxInd(p);
			val = p[ind];
			h = f[ind];
			
			
		    //%************************************************************
		   // %***********  Band pass, Derivative & Find Max  *************
		  //  %************************************************************
			
			x21=lowpass_principal(sig, fs, 1.5*h);
		    x22=highpass_filter(x21, 0.25);	    
		    
		    //%Derivative
		    double[] temp = diff(x22);
		    x23 = new double[temp.length];
		    for(int index = 0; index < temp.length; index++)
		    	x23[index] = -1 * temp [index];
		    
		    p2 = DetectMaxima_Jinseok(x23,50);
		    
		    for(int index = 0; index < p2.length; index++)
		    {
		    	//System.out.print(p2[index] + " , ");
		    }
		    //System.out.println("\n" + p2.length);
		    
		    //%************************************************************
		    //%***********  Match peaks to Band pass signal  **************
		    //%************************************************************
		    
		    
		    //%%%%%%%%%%%%%%%%%%%%%%% POST AND PRE DELAT %%%%%%%%%%%%%%%%%%5
		    // 0.3 has extra peaks detected, change to 0.4 for more accurate results
		    // Pd represents how close two peaks can be
		    double Pd = Math.round(fs*0.4/2);
		    double delay_pre = Pd + 2;
		    double delay_post = Pd -2;
		    double[] sig_sm,max_cand1,locs,tlocs;
			double max_cand2,max_cand3;
		    
			int Ncp = 1;
			tlocs = new double[p2.length];
			
			for(j = 1; j <= p2.length; j ++)
			{
				sig_sm = new double[(int) Math.floor((p2[j - 1] + delay_post) - (p2[j - 1] - delay_pre)) + 1];
				
				if(p2[j - 1] > delay_pre && p2[j - 1] < sig.length - delay_post)
				{
					for(int index = 0; index < sig_sm.length; index++)
					{
						sig_sm[index] = sig[(int)(p2[j- 1] - delay_pre) - 1 + index];
					}
					
					max_cand1 = DetectMaxima_Jinseok(sig_sm,50);
					if(max_cand1.length > 1)
					{
						max_cand2 = multiple_peak_elimination(max_cand1, sig_sm);
					}else if(max_cand1.length == 0)
					{
						max_cand2 = Double.NaN;
					}else
					{
						max_cand2 = max_cand1[0];
					}
					
					max_cand3 = max_cand2 - delay_pre+p2[j-1] - 1;
					/*//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
					for(int index = 0; index < sig_sm.length; index++)
				    {
				    	//System.out.print(sig_sm[index] + " , ");
				    }
				    System.out.println("\n" + sig_sm.length);
				    *///%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
					
					if((max_cand3 > Pd) && (max_cand3<sig.length - Pd +1))
					{
						temp = new double[(int) (Pd*2 + 1)];
							for(int index = 0; index < temp.length; index++)
							{
								temp[index] = sig[(int) (max_cand3-Pd + index - 1)];
							}
							temp[(int) Pd] = -214748364;
							
							boolean testCond = true;
							for(int index = 0; index < temp.length; index++)
							{
								if(sig[(int) max_cand3 - 1] < temp[index])
								{
									testCond = false;
								}
							}
							
							if(testCond)
							{
								tlocs[Ncp - 1] = max_cand3;
								Ncp = Ncp + 1;
							}
					}
				}
			}
			locs = new double[Ncp - 1];
			System.arraycopy(tlocs, 0, locs, 0, Ncp - 1);
			
			double pc_G_false;
			
			double[] pc_G = new double[locs.length];
			for(int index = 0; index < locs.length ; index++)
			{
				pc_G[index] = locs[index] + j_start + i - 1;
			}
			
			pc_G_false = Eliminate_overlapped_peaks(pc_G,pc_G_pre,sig,Pd,i,j_start);
			
			
			for(int index = 0; index < pc_G.length; index++)
			{
				final_result[(int) (pc_G[index] - 1)] = 1;
			}
			if(!Double.isNaN(pc_G_false))
			{
				final_result[(int) pc_G_false - 1] = 0;
			}
			pc_G_pre = new double[pc_G.length];
			System.arraycopy(pc_G, 0, pc_G_pre, 0, pc_G.length);
			
			i = i + sh; //%shift 1s
			
		}
		
		// Post Processing
		int[] temp_final = new int[final_result.length];
		int final_len = 0;
		for(int index = 0; index < temp_final.length; index ++)
		{
			if(final_result[index] == 1)
			{
				// Makes sure to remove the +1, or if you need it to match with matlab add it;
				temp_final[final_len] = index;
				final_len++;
			}
		}
		int[] final2 = new int[final_len];
		System.arraycopy(temp_final, 0, final2, 0, final2.length);
		
		return final2;
	}

	//set : filtered한 HRarr , fs : 30이 넘오오고 있음
	public static int[] Peak_Detection_for_hrArr(double[] set, double fs)
	{
		int counter = 0;
		//Extra variable for initialization
		double TH;
		
		//Does initial Setup of data
		double[] sig_all = new double[set.length];
		for(int i=0; i<set.length;i++)
			sig_all[i] = set[i];
		
		int Lx = sig_all.length;
		double[] sig_temp = Arrays.copyOf(sig_all, Lx);
		double[] final_result = new double[Lx];
		
		int sh = (int) fs; // 30
		int w = (int) (fs*2); // 60
		
		double[] pc_G_pre = new double[0];
		double TH_maxtomedian = 1000; // 1000
		
		int i= 1;
		int j =1;

		double[] sig;
		sig = new double[w];
		int j_start;
		
		double[] x11,x12,x21,x22,x23,p2;
		
		ArrayList<Object> result;
		while(i<Lx-w)
		{
			sig = new double[w]; // 배열의 크기 = 60
			System.arraycopy(sig_all, i-1, sig, 0, w); // 원본으로 부터 60만큼 복사함
			TH = TH_maxtomedian * 10; // 30000
			
			//eliminate_DCoff
			result = eliminate_DCoff(sig,TH);
			sig = (double[]) result.get(0);
			
			TH_maxtomedian = (Double) result.get(1);
			j_start = (Integer) result.get(2);
			//eliminate_DCoff
			
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			//%%%%% Band pass, Derivatives & Find Max %%%%%%%%%%%%%%
			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			
			x11 = lowpass_principal(sig,fs,5);
			x12 = highpass_filter(x11,0.5);	
			
			int Nfft = 256;
			double[] p,f;
			double val,h;
			int ind;
			ArrayList<double []> psd_result = psd_welch(x12,x12.length,fs,Nfft);
			p = psd_result.get(0);
			f = psd_result.get(1);
			ind = maxInd(p);
			val = p[ind];
			h = f[ind];
			
			
		    //%************************************************************
		   // %***********  Band pass, Derivative & Find Max  *************
		  //  %************************************************************
			
			x21=lowpass_principal(sig, fs, 1.5*h);
		    x22=highpass_filter(x21, 0.25);	    

		    //%Derivative
		    double[] temp = diff(x22);
		    x23 = new double[temp.length];
		    
		    for(int index = 0; index < temp.length; index++){
		    	x23[index] = -1 * temp [index];
		    }
		    p2 = DetectMaxima_Jinseok(x23,50);
		    
		    //%************************************************************
		    //%***********  Match peaks to Band pass signal  **************
		    //%************************************************************
		    
		    
		    //%%%%%%%%%%%%%%%%%%%%%%% POST AND PRE DELAT %%%%%%%%%%%%%%%%%%5
		    // 0.3 has extra peaks detected, change to 0.4 for more accurate results
		    // Pd represents how close two peaks can be
		    double Pd = 7; // 원본 fs*0.4/2 // 수정1안 5
		    double delay_pre = 12; // 원본 Pd + 2 // 수정 1안 8
		    double delay_post = 6; // 원본 pd - 2 // 수정 1안 4
		    double[] sig_sm,max_cand1,locs,tlocs;
			double max_cand2,max_cand3;
		    
			int Ncp = 1;
			tlocs = new double[p2.length];
			
			for(j = 1; j <= p2.length; j ++)
			{
				sig_sm = new double[(int) Math.floor((p2[j - 1] + delay_post) - (p2[j - 1] - delay_pre)) + 1];
				
				if(p2[j - 1] > delay_pre && p2[j - 1] < sig.length - delay_post)
				{
					for(int index = 0; index < sig_sm.length; index++)
					{
						sig_sm[index] = sig[(int)(p2[j- 1] - delay_pre) - 1 + index];
					}
					
					max_cand1 = DetectMaxima_Jinseok(sig_sm,50);
					if(max_cand1.length > 1)
					{
						max_cand2 = multiple_peak_elimination(max_cand1, sig_sm);
					}else if(max_cand1.length == 0)
					{
						max_cand2 = Double.NaN;
					}else
					{
						max_cand2 = max_cand1[0];
					}
					
					max_cand3 = max_cand2 - delay_pre+p2[j-1] - 1;
					
					if((max_cand3 > Pd) && (max_cand3<sig.length - Pd +1))
					{
						temp = new double[(int) (Pd*2 + 1)];
							for(int index = 0; index < temp.length; index++)
							{
								temp[index] = sig[(int) (max_cand3-Pd + index - 1)];
							}
							temp[(int) Pd] = -214748364;
							
							boolean testCond = true;
							for(int index = 0; index < temp.length; index++)
							{
								if(sig[(int) max_cand3 - 1] < temp[index])
								{
									testCond = false;
								}
							}
							
							if(testCond)
							{
								tlocs[Ncp - 1] = max_cand3;
								Ncp = Ncp + 1;
							}
					}
				}
			}
			locs = new double[Ncp - 1];
			System.arraycopy(tlocs, 0, locs, 0, Ncp - 1);
			
			double pc_G_false;
			
			double[] pc_G = new double[locs.length];
			for(int index = 0; index < locs.length ; index++)
			{
				pc_G[index] = locs[index] + j_start + i - 1;
			}
			
			pc_G_false = Eliminate_overlapped_peaks(pc_G,pc_G_pre,sig,Pd,i,j_start);
			
			
			for(int index = 0; index < pc_G.length; index++)
			{
				final_result[(int) (pc_G[index] - 1)] = 1;
			}
			if(!Double.isNaN(pc_G_false))
			{
				final_result[(int) pc_G_false - 1] = 0;
			}
			pc_G_pre = new double[pc_G.length];
			System.arraycopy(pc_G, 0, pc_G_pre, 0, pc_G.length);
			
			i = i + sh; //%shift 1s
		}
		
		// Post Processing
		int[] temp_final = new int[final_result.length];
		int final_len = 0;
		for(int index = 0; index < temp_final.length; index ++)
		{
			if(final_result[index] == 1)
			{
				// Makes sure to remove the +1, or if you need it to match with matlab add it;
				temp_final[final_len] = index;
				final_len++;
			}
		}
		int[] final2 = new int[final_len];
		System.arraycopy(temp_final, 0, final2, 0, final2.length);
		
		return final2;
	}

	public static ArrayList<Object> eliminate_DCoff(double[] sig, double TH)
	{
		
		ArrayList<Object> result = new ArrayList<Object>();
		double[] new_sig;
		double TH_maxtomedian;
		int j_start;
		//Start of the function when compared to matlab code
		double[] diff_sig = diff(sig); // sig[i+1] - sig[i]를 계산
		double[] diff_abs_sig = abs(diff(sig)); //절댓값
		
		//instatiation of maxtomedian
		double[] maxtomedian = new double[diff_abs_sig.length];
		double med_diff = median(diff_abs_sig); // 중앙값 계산
		
		//Ensures that the app does not crash with no signal, added by Jeff
		//시그널이 없을 때 오류가 나지 않게 보장하다
		if(med_diff == 0)
			med_diff = 1;
		
		for(int i = 0; i < diff_abs_sig.length; i++)
		{
			maxtomedian[i] = diff_abs_sig[i]/med_diff;
			//sig[i+1] - sig[i]의 절댓값한 것을 중앙값으로 나눈다
		}
		
		//Instatiation completed.
		int Ln_sig = sig.length;
		int Ln_diff = diff_sig.length;
		
		int j=1,i=1;
		double tmp[] = new double[Ln_diff];
		
		//TH보다 큰 수를 NaN으로 정한 후 tmp에 저장
		//여기서 TH는 30000
		//즉, 비정상적으로 튀는 값 제거
		while(i<=Ln_diff)
		{
			if(maxtomedian[i-1] < TH)
			{
				tmp[j-1] = diff_sig[i-1];
			}else
			{
				tmp[j-1] = Double.NaN;
			}
			j++;
			i++;
		}
			
		i = 1;
		j_start = 0;
		 
		// sig[i+1] - sig[i]가 -1000이하인 구간을 찾음
		//그러나 이것은 수치상으로 절대 발생할 수 없음
		//tmp 배열 자체가 sig[i+1] - sig[i] 인데 차이가 1000을 넘을 수 없음
		//sig 배열은 HRarr 이고 HRarr 은 averageRGB임
		while(tmp[i-1] <= -1000)
		{
			i ++;
			j_start ++;
		}
		
		i = tmp.length;
		int j_end = 0;
		
		while(tmp[i-1] <= -1000)
		{
			i = i-1;
			j_end = j_end + 1;
		}
		
		double tmp1[] = new double[tmp.length - j_end - j_start];
		//tmp에서 체크한 부분을 제외하여 tmp1에 저장
		System.arraycopy(tmp, j_start, tmp1, 0, tmp.length - j_end - j_start);
		
		//Splining of tmp1 eliminating NaN
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5
		int point = 0;
		double[] index = new double[tmp1.length];
		double[] tmp2 = new double[tmp1.length];
		//This is used to remove all NaN components
		for(int count = 0; count < tmp1.length ; count++)
		{
			if(Double.isNaN(tmp1[count]))
			{
				//Do nothing ignore that value
			}else
			{
				//해당 인덱스 값 저장
				index[point] = count;
				//tmp1 배열에서 NaN이 아닌 값들만 tmp2에 저장
				tmp2[point] = tmp1[count];
				point++;
			}
		}
		double[] index2 = new double[point];
		double[] tmp3 = new double[point];
		System.arraycopy(index, 0, index2, 0, point);
		System.arraycopy(tmp2, 0, tmp3, 0, point);
		
		Spline tmp1_spline = new Spline(index2, tmp3);
		
		for(int count = 0; count < tmp1.length; count++)
		{
			tmp1[count] = tmp1_spline.spline_value(count);
		}
		
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		
		new_sig = new double[tmp1.length+1];
		new_sig[0] = sig[j_start];	// 아마 시작점을 설정해주는게 아닌가?
		for(i = 1; i<tmp1.length+1; i++)
		{
			new_sig[i] = new_sig[i-1] + tmp1[i-1];
		}
		
		double[] new_diff_sig = diff(new_sig);
		double[] new_diff_abs_sig = abs(diff(new_sig));
		
		//Determining TH_maxtoMedian
		double[] temp_new_diff = new double[new_diff_abs_sig.length];
		double med = median(new_diff_abs_sig);
		for(int count = 0; count < new_diff_abs_sig.length; count++)
		{
			temp_new_diff[count] = new_diff_abs_sig[count]/med;
		}
		TH_maxtomedian = max(temp_new_diff);

		result.add(new_sig);
		result.add(TH_maxtomedian);
		result.add(j_start);
		
		return result;
	}
	
	public static double[] lowpass_principal(double[] signal, double fs, double fc)
	{
		//if the maximum of the signal is 0, just return the signal.
		if(max(signal) == 0)
		{
			return signal;
		}
		
		double M,sum;
		double [][] m;
		double [] coefs,g;
		M=blackman_lookup_table(fc,fs);
		
		coefs = blackman_window(M);
		coefs[0] = 0;
		coefs[coefs.length - 1] = 0;
		
		m = new double[signal.length][coefs.length];
		// Convolution
		for(int i = 0; i < signal.length; i++)
		{
			for(int j = 0; j < coefs.length; j++)
			{
				m[i][j] = signal[i] * coefs[j];
			}
		}
		
		int n = -1*(signal.length-1);
		g = new double[signal.length+coefs.length-1];
		int col;
		for (int r=1; r < signal.length+coefs.length-1; r++)
		{
			sum = 0;
			col = n;
			
			for(int row = 0; row < m.length; row++)
			{
				if(0<=row && row <m.length && 0<=col && col<m[0].length)
				{
					sum = sum + m[row][col];
				}
				col++;
			}
		    g[r-1]=sum;
		    n=n+1;
		}
		
		double[] ytemp = new double[g.length];
		for(int ind = 0; ind < g.length;ind++)
		{
			ytemp[g.length - 1 - ind] = g[ind];
		}
		
		int nLength = (int) (Math.ceil((double)(ytemp.length)-(double)(coefs.length)/2) - Math.ceil((double)(coefs.length)/2) + 1);
		double[] y = new double[nLength];
		System.arraycopy(ytemp, (int) Math.ceil(coefs.length/2), y, 0, nLength);
		
		int ci_length = (int) (Math.floor((M-1)/2)*2);
		double[] ci = new double[ci_length];
		
		// Need to make 2 loops for ci.;
		for(int index = 0; index < ci_length/2; index++)
		{
			//ci=[1:((M-1)/2) (length(signal)+1-(M-1)/2):length(signal)];
			// this code copied from matlab file is replicated here in java
			ci[index] = 1 + index;
		}
		for(int index = 0; index < ci_length/2; index++)
		{
			//ci=[1:((M-1)/2) (length(signal)+1-(M-1)/2):length(signal)];
			// this code copied from matlab file is replicated here in java
			ci[index + ci_length/2] = signal.length + 1 - ((M-1)/2) + index;
		}
		
		double in,temp1,temp2,sum1,sum2;
		double[] xi,bi;
		for(int c1 = 0; c1< ci.length; c1++)
		{
			in = ci[c1];
			
			// Xi Iteration
			temp1 = Math.max(1,in-(M-1)/2);
			temp2 = Math.min(signal.length,in+(M-1)/2);
			
			xi = new double[(int) (Math.floor(temp2 - temp1)+1)];
			
			for(int index = 0; index < xi.length; index++)
			{
				xi[index] = temp1 + index;
			}
			
			
			// Bi Interation
			temp1 = ((M-1)/2+1)+(Math.max(1-in,-(M-1)/2));
			temp2 = ((M-1)/2+1) + Math.min(signal.length-in,(M-1)/2);
			
			bi = new double[(int) (Math.floor(temp2 - temp1)+1)];
			
			for(int index = 0; index < bi.length; index++)
			{
				bi[index] = temp1 + index;
			}
			
			
			sum1 = 0;
			sum2 = 0;
			for(int index = 0; index < bi.length; index++)
			{
				sum1 = sum1 + coefs[(int) bi[index] - 1];
			}
			for(int index = 0; index < xi.length; index++)
			{
				
				sum2 = sum2 + (signal[(int) xi[index] - 1]*coefs[(int) bi[index] - 1]);
			}
			
			//this if statement was added to avoid software crashes and should not be needed
			if(in > y.length)
			{
				in = y.length;
			}else if(in < 1)
			{
				in = 1;
			}
			
			y[(int) in - 1] = sum2/sum1;
			
		}
		
		return y;
	}
	
	public static double blackman_lookup_table(double fc, double fs)
	{
		double wc = fc/(fs/2);
		
		double [][] A =
		{{5, 0.410889},
        {7, 0.274048},
        {9, 0.205505},
        {11, 0.164368},
        {13, 0.136963},
        {15, 0.117432},
        {17, 0.102722},
        {19, 0.091309},
        {21, 0.082214},
        {23, 0.074707},
        {25, 0.068481},
        {27, 0.063232},
        {29, 0.058716},
        {31, 0.054810},
        {33, 0.051392},
        {35, 0.048340},
        {37, 0.045654},
        {39, 0.043274},
        {41, 0.041077},
        {43, 0.039124},
        {45, 0.037354},
        {47, 0.035706},
        {49, 0.034241},
        {51, 0.032898},
        {53, 0.031616},
        {55, 0.030457},
        {57, 0.029358},
        {59, 0.028320},
        {61, 0.027405},
        {63, 0.026489},
        {65, 0.025696},
        {67, 0.024902},
        {69, 0.024170},
        {71, 0.023499},
        {73, 0.022827},
        {75, 0.022217},
        {77, 0.021606},
        {79, 0.021057},
        {81, 0.020569},
        {83, 0.020020},
        {85, 0.019592},
        {87, 0.019104},
        {89, 0.018677},
        {91, 0.018250},
        {93, 0.017883},
        {95, 0.017456},
       	{97, 0.017151},
       	{99, 0.016785},
       	{101, 0.016418},
       	{105, 0.015808},
       	{107, 0.015503},
       	{111, 0.014954},
       	{115, 0.014404},
       	{119, 0.013916},
       	{121, 0.013672},
       	{125, 0.013245},
       	{129, 0.012817},
       	{133, 0.012451},
       	{137, 0.012085},
       	{141, 0.011719},
       	{147, 0.011230},
       	{151, 0.010986},
       	{155, 0.010681},
       	{161, 0.010254},
       	{165, 0.010010},
       	{171, 0.009644},
       	{175, 0.009460},
       	{181, 0.009155},
       	{187, 0.008850},
       	{193, 0.008545},
       	{199, 0.008301},
       	{205, 0.008057},
       	{211, 0.007813},
       	{219, 0.007568},
       	{225, 0.007324},
       	{231, 0.007141},
       	{239, 0.006897},
       	{247, 0.006653},
       	{255, 0.006470},
       	{263, 0.006287},
       	{271, 0.006104},
       	{279, 0.005920},
       	{287, 0.005737},
       	{297, 0.005554},
       	{305, 0.005432},
       	{315, 0.005249},
       	{325, 0.005066},
       	{335, 0.004944},
       	{345, 0.004761},
       	{357, 0.004639},
       	{367, 0.004517},
       	{379, 0.004333},
       	{391, 0.004211},
       	{403, 0.004089},
       	{415, 0.003967},
       	{429, 0.003845},
       	{443, 0.003723},
       	{457, 0.003601},
       	{471, 0.003479},
       	{485, 0.003418},
       	{501, 0.003296},
       	{517, 0.003174},
       	{533, 0.003113},
       	{549, 0.002991},
       	{567, 0.002930},
       	{583, 0.002808},
       	{603, 0.002747},
       	{621, 0.002625},
       	{641, 0.002563},
       	{661, 0.002502},
       	{681, 0.002441},
       	{703, 0.002319},
       	{725, 0.002258},
       	{747, 0.002197},
       	{771, 0.002136},
       	{795, 0.002075},
       	{819, 0.002014},
       	{845, 0.001953},
       	{871, 0.001892},
       	{899, 0.001831},
       	{927, 0.001770},
       	{957, 0.001709},
       	{1017, 0.001648},
       	{1049, 0.001587},
       	{1081, 0.001526},
       	{1115, 0.001465},
       	{1187, 0.001404},
       	{1223, 0.001343},
       	{1301, 0.001282},
     	{1343, 0.001221},
     	{1429, 0.001160},
     	{1519, 0.001099},
     	{1617, 0.001038},
     	{1719, 0.000977},
     	{1829, 0.000916},
     	{1945, 0.000854},
     	{2133, 0.000793},
     	{2341, 0.000732},
     	{2491, 0.000671},
     	{2817, 0.000610},
     	{3091, 0.000549},
     	{3497, 0.000488},
     	{4083, 0.000427},
     	{4765, 0.000366},
     	{5915, 0.000305},
     	{7573, 0.000244},
     	{10001, 0.000183}};
		
		double val,diff,temp;
		val = A[0][0];
		diff = Math.abs(wc - A[0][1]);
		for(int count = 0; count < A.length; count++)
		{
			temp = Math.abs(wc - A[count][1]);
			if(temp < diff)
			{
				diff = temp;
				val = A[count][0];
			}
		}
		return val;
	}
	
	public static double[] blackman_window(double M)
	{
		double alpha = 0.16;
		double a0 = (1-alpha)/2;
		double a1 = 0.5;
		double a2 = alpha/2;
		
		double[] coefs = new double[(int) (M)];
		for(double n = 0; n <= M-1; n++)
		{
			//System.out.println(n + "  ++++++++++ " + coefs.length);
			//Log.e("Blackman window", n + "");
			coefs[(int) n] = (a0-a1*(Math.cos((2*Math.PI*n)/(M-1))) + a2*(Math.cos((4*Math.PI*n)/(M-1))));
		}
		
		double sum = 0;
		for (double i : coefs) {
		 sum += i;
		}
		
		for(int count = 0; count < coefs.length; count++)
		{
			coefs[count] = coefs[count]/sum; 
		}
		
		return coefs;
	}
	
	public static double[] highpass_filter(double[] signal, double fc)
	{
		double[] a = new double[6];
		double[] b = new double[6];
		double[] zi = new double[5];
		double[] y;
		if (fc==0.5)
		{
			a[0]=1;
			a[1]=-4.60699664684803;
			a[2]=8.50751263449196;
			a[3]=-7.86788770781740;
			a[4]=3.64220253554851;
			a[5]=-0.674746958146318;
		   
			b[0]=0.824665274293227;
			b[1]=-4.11207512603613;
			b[2]=8.21293284109676;
			b[3]=-8.21293284109676;
			b[4]=4.11207512603613;
			b[5]=-0.824665274293227;
		    
			zi[0]=-0.824665274306237;
			zi[1]=3.28740985178983;
			zi[2]=-4.92552298941761;
			zi[3]=3.28740985178150;
			zi[4]=-0.824665274302005;
		}else if( fc == 0.25)
		{
			a[0]=1;
			a[1]=-4.80408432849615;
			a[2]=9.23570696080354;
			a[3]=-8.88100025696535;
			a[4]=4.27127693759408;
			a[5]=-0.821896436153646;
			
			b[0]=0.907459640661287;
			b[1]=-4.53420467473151;
			b[2]=9.06531814461358;
			b[3]=-9.06531814461359;
			b[4]=4.53420467473152;
			b[5]=-0.907459640661288;
			
			zi[0]=-0.907459641778597;
			zi[1]=3.62674503832057;
			zi[2]=-5.43857311661216;
			zi[3]=3.62674503792426;
			zi[4]=-0.907459641579602;
		}
		
		int nfact = 3* (Math.max(a.length, b.length)-1);
		
		
		/*
			% Non-causal filtering                             
			x=[2*signal(1)-signal(nfact+1:-1:2); signal; 2*signal(end)-signal(end-1:-1:end-nfact)];
			*/
			double[] temp1 = new double[nfact];
			double[] temp2 = new double[nfact];
			for(int index = 0; index < nfact; index++)
			{
				temp1[index] = 2*signal[0] - signal[nfact - index];
				//2* signal의 마지막 값 - signal의 마지막값 전 - 인덱스
				temp2[index] = 2*signal[signal.length - 1] - signal[signal.length - 2 - index];
			}
			double[] x = new double[nfact*2 + signal.length];
			System.arraycopy(temp1, 0, x, 0, temp1.length);
			System.arraycopy(signal, 0, x, temp1.length, signal.length);
			System.arraycopy(temp2, 0, x, temp1.length + signal.length, temp2.length);
			
			y=filter_DFIITS(x, b, a, zi);
			
			double[] ytemp = new double[y.length];
			for(int ind = 0; ind < y.length;ind++)
			{
				ytemp[y.length - 1 - ind] = y[ind];
			}
			y = ytemp;
			
			y = filter_DFIITS(y, b, a, zi);
			
			double[] sig_filt = new double[y.length - 2*nfact]; 
			for(int ind = 0; ind < sig_filt.length;ind++)
			{
				sig_filt[ind] = y[y.length-nfact-1-ind];
			}
		
		return sig_filt;
	}
	
	public static double[] filter_DFIITS(double[] x, double[] b, double[] a, double[] zi)
	{
		//% Filtering
		double[] y = new double[x.length];
		double[] z = new double[zi.length + 1];
		double[] xt = new double[b.length - 1];
		double[] yt = new double[a.length - 1];
		
		for(int index = 0; index < zi.length; index++)
		{
			z[index] = zi[index] * x[0];
		}
		z[z.length-1] = 0;
		
		for(int m = 0; m < x.length; m++)
		{
			y[m] = b[0]*x[m] + z[0];
			
			//indexes start at one because in matlab they start at 2
			for(int index = 1; index < b.length; index++)
			{
				xt[index-1] = b[index] * x[m];
			}
			for(int index = 1; index < a.length; index++)
			{
				yt[index-1] = a[index] * y[m];
			}			
			
			//z=xt+z(2:end)-yt;
		    //z=[z; 0];
			for(int index = 0; index < z.length - 1; index++)
			{
				z[index] = xt[index] + z[index + 1] - yt[index];
			}
			z[z.length - 1] = 0;
		}		
		return y;
	}
	
	
	public static ArrayList<double[]> psd_welch(double[] signal, int Lw, double fs, int Nfft)
	{
		double[] win,x, xStart,xEnd;
		double noverlap,k,N,LminusOverlap;
		
		x = new double[signal.length];
		System.arraycopy(signal, 0, x, 0, signal.length);
		
		win = hamming_window(Lw);

		N = x.length;
		noverlap = Math.round((0.5*(double)Lw));
		k = (N-noverlap)/((double)Lw - noverlap);
		k = Math.round(k);
		
		double[] Sxx = new double[Nfft];
		Arrays.fill(Sxx, 0);
		
		LminusOverlap = (double)Lw - noverlap;
		xStart = new double[(int) k];
		xEnd = new double[(int) k];
		for(int ind = 0; ind < k; ind++)
		{
			xStart[ind] = 1 + LminusOverlap * ind;
			xEnd[ind] = xStart[ind] + (double)Lw - 1;
		}
		
		double[] xi,xw,Sxxk;
		double pw,sum;
		Complex[] Xx;
		
		for(int i = 0; i < k; i++)
		{
			xi = new double[Lw];
			xw = new double[xi.length];
			Xx = new Complex[Nfft];			
			
			for(int ind = 0; ind < Lw; ind++)
			{
				xi[ind] = x[(int) (xStart[i] + ind) - 1];
				xw[ind] = xi[ind] * win[ind];
			}
			
			Xx = fft_code(xw,Nfft);
			
			pw = 0;
			for(int ind = 0; ind < win.length; ind ++)
			{
				pw += Math.pow(win[ind],2);
			}

			Sxxk = new double[Xx.length];
			for(int ind = 0; ind < Xx.length; ind ++)
			{
				Sxxk[ind] =( Xx[ind].times(Xx[ind].conjugate())).re()/pw;
				Sxx[ind] = (Sxx[ind] + Sxxk[ind])/k;
			}
			
		}
		
		double fr,Nyq,half_res;
		double[] freq, w;
		int halfNPTS;
		
		// Generate the frequency vector
		fr = fs/Nfft;
		freq = new double[Nfft];
		for(int ind = 0; ind < freq.length; ind ++)
		{
			freq[ind] = fr*ind;
		}
		
		//Fixing frequency points near pi and 2pi
		Nyq = fs/2;
		half_res = fr/2;
		
		//Determine if Nfft is odd and calculate half of Nfft
		w = new double[Nfft];
		if(Nfft%2> 0)
		{
			halfNPTS = (Nfft+1)/2;
			w[halfNPTS-1] = Nyq-half_res;
			w[halfNPTS] = Nyq+half_res;
		}else
		{
			halfNPTS=(Nfft/2)+1;
			w[halfNPTS] = Nyq;
		}
		w[Nfft - 1] = fs-fr;
		
		
		double[] interval, Sxx_unscaled;
		// Compute the 1-sided PSD, i.e. taking only [0,pi] or [0,pi), and scale
		if(Nfft%2 > 0)
		{
			interval = new double[(Nfft+1)/2];
			for(int ind = 0; ind < interval.length;ind++)
			{
				interval[ind] = 1 + ind;
			}
			
			Sxx_unscaled = new double[interval.length];
			for(int ind = 0; ind < Sxx_unscaled.length;ind++)
			{
				Sxx_unscaled[ind] = Sxx[(int) (interval[ind] - 1)];
			}
			
			
			Sxx = new double[interval.length];
			Sxx[0] = Sxx_unscaled[0];
			for(int ind = 1; ind < Sxx.length;ind++)
			{
				Sxx[ind] = 2* Sxx_unscaled[ind];
			}
			
			
		}else
		{
			interval = new double[Nfft/2 + 1];
			for(int ind = 0; ind < interval.length;ind++)
			{
				interval[ind] = 1 + ind;
			}
			
			Sxx_unscaled = new double[interval.length];
			for(int ind = 0; ind < Sxx_unscaled.length;ind++)
			{
				Sxx_unscaled[ind] = Sxx[(int) (interval[ind] - 1)];
			}
			
			// a minus one was added to the end condition based on matlab algorithm
			Sxx = new double[interval.length];
			Sxx[0] = Sxx_unscaled[0];
			Sxx[Sxx.length - 1] = Sxx_unscaled[Sxx.length - 1];
			for(int ind = 1; ind < Sxx.length - 1;ind++)
			{
				Sxx[ind] = 2*Sxx_unscaled[ind];
			}
			
		}
		
		double[] PSD = new double[Sxx.length];
		double[] newFreq = new double[interval.length];
		
		for(int ind = 0; ind < newFreq.length;ind++)
		{
			newFreq[ind] = freq[(int) interval[ind] - 1];
		}
		for(int ind = 0; ind < PSD.length;ind++)
		{
			PSD[ind] = Sxx[ind]/fs;
		}
		
		ArrayList<double[]> result = new ArrayList<double[]>();
		result.add(0,PSD);
		result.add(1,newFreq);
		
		return result;
	}
	
	public static double[] hamming_window(int M)
	{
		double[] coefs = new double[M];
		for(int n = 0; n < M; n++)
		{
			coefs[n]= 0.54-(0.46*(Math.cos((2*Math.PI*n)/(M-1))));
		}
		double temp = max(coefs);
		for(int index = 0; index < coefs.length; index++)
		{
			coefs[index] = coefs[index]/temp;
		}
		
		return coefs;
	}
	
	
	public static Complex[] fft_code(double[] sig, int inNfft)
	{
		int Nx;
		double[] zeros_pad,x;
		double p;
		int Nfft = inNfft;
		
		Nx = sig.length;
		
		if (Nx < Nfft)
		{
			zeros_pad = new double[Nfft - Nx];
			Arrays.fill(zeros_pad,0);
			
			x=new double[sig.length + zeros_pad.length];
			System.arraycopy(sig, 0, x, 0, sig.length);
			System.arraycopy(zeros_pad, 0, x, sig.length, zeros_pad.length);
		}else if (Nx == Nfft)
		{
			x = new double[sig.length];
			System.arraycopy(sig, 0, x, 0, sig.length);
		}else
		{
			p = Math.log(Nx)/Math.log(2);
			if(Math.round(p) == p)
			{
				x = new double[sig.length];
				System.arraycopy(sig, 0, x, 0, sig.length);
			}else
			{
				p = Math.ceil(p);
				Nfft = (int) Math.pow(2, p);
				zeros_pad = new double[Nfft - Nx];
				Arrays.fill(zeros_pad,0);
				
				x=new double[sig.length + zeros_pad.length];
				System.arraycopy(sig, 0, x, 0, sig.length);
				System.arraycopy(zeros_pad, 0, x, sig.length, zeros_pad.length);
			}
		}
				
		int N,desp;
		double M;
		double[] ffte;
		
		N = x.length;
		
		M = N/2;
		desp = 0;
		
		ffte = new double[N];
		Arrays.fill(ffte, 0);
		
		while(M > 1)
		{
			while(desp < N)
			{
				for(int i = 0; i< M; i ++)
				{
					//In matlab every index ha a +1, in java that was removed
					ffte[i+desp] = x[2*i + desp];
					ffte[(int) (i+M+desp)] = x[2*i+1+desp];
				}
				desp = (int) (desp + 2 * M);
			}
			System.arraycopy(ffte, 0, x, 0, x.length);
			desp = 0;
			// !!!!!!!!!!!!! IF THERE IS AN ERROR TRY MAKING M A DOUBLE
			M = M/2;
		}
		
		M = 1;
		desp = 0;
		int upar,uimp;
		Complex W;
		
		//Converts x and ffte for complex use, from this point on only use cX and cffte.
		Complex[] cX = arrayD2C(x);
		Complex[] cffte = arrayD2C(ffte);
		Complex[] F = new Complex[cffte.length];
		
		while( M <= N/2)
		{
			while(desp < N)
			{
				for(int u = 1; u <= M; u++)
				{
					upar = u + desp;
					uimp = (int) (u + M + desp);
					W = new Complex(Math.cos(-2*Math.PI*(u-1)/(2*M)),Math.sin(-2*Math.PI*(u-1)/(2*M)));
					F[upar - 1] = cffte[upar - 1].plus(cffte[uimp - 1].times(W));
					F[uimp - 1] = cffte[upar - 1].minus(cffte[uimp - 1].times(W));
				}
				desp = (int) (desp + 2*M);
			}
			System.arraycopy(F, 0, cffte, 0, cffte.length);
			desp = 0;
			M = 2*M;
		}
		return cffte;
	}
	
	public static double[] DetectMaxima_Jinseok(double[] x, double th)
	{
		double[] y;
		double[][] m;
		int Lx, k; 
		
		Lx = x.length;
		k = 1;
		double[][] tempM= new double[2][Lx - 2];
		for(int i = 2; i < Lx; i++)
		{
			if ((x[i-2] <= x[i - 1]) && (x[i - 1] >= x[i]))
			{
				tempM[0][k-1] = i;
				tempM[1][k-1] = x[i - 1];
				k++;
			}
		}
		
		// Makes m the correct length
		m = new double[2][k - 1];
		System.arraycopy(tempM[0], 0, m[0], 0, k-1);
		System.arraycopy(tempM[1], 0, m[1], 0, k-1);
		
		
		//Prcentile Function replication in the cases of this program just gets median
		double TH = median(x);
		int tmp = m.length;
		int Lth = m[0].length;
		int i = 1;
		
		double[] tempY = new double[Lth];
		for(k = 1; k <= Lth; k++)
		{
			if(m[1][k - 1] >= TH)
			{
				tempY[i - 1] = m[0][k - 1];
				i = i + 1;
			}
		}
		
		y = new double[i - 1];
		System.arraycopy(tempY, 0, y, 0, i-1);
		
		
		return y;
	}
	
	public static double multiple_peak_elimination(double[] max_cand1, double[] sig_sm)
	{
		double max_cand2 = Double.NaN;
		double Ln_max = max_cand1.length;
		double max_temp;
		
		if(Ln_max > 1)
		{
			max_temp = -1000;
			for(int jj = 1; jj <= Ln_max ; jj++)
			{
				if(sig_sm[(int) (max_cand1[jj - 1] - 1)] >= max_temp)
				{
					max_cand2 = max_cand1[jj - 1];
					max_temp = sig_sm[(int) (max_cand1[jj - 1] - 1)];
				}
			}
		}
		
		
		return max_cand2;
	}
	
	public static double Eliminate_overlapped_peaks(double[] pc_G, double[] pc_G_pre, double[] sig, double Pd, int start, double j_start)
	{
		double pc_G_false = 0;
		double invalid = Double.NaN;
		
		double[] loc_pre = new double[pc_G_pre.length];
		for(int index = 0; index < loc_pre.length; index++)
		{
			loc_pre[index] = pc_G_pre[index] - start + 1 - j_start;
		}
		
		double[] loc_current = new double[pc_G.length];		
		for(int index = 0; index < pc_G.length; index++)
		{
			//loc_current=pc_G-start+1;
			loc_current[index] = pc_G[index] - start + 1;
		}
		
		//ind=find(loc_pre>0);
		int[] temp_ind = new int[loc_pre.length];
		int gtz_ind = 0;
		for(int index = 0; index < loc_pre.length; index++)
		{
			//ind=find(loc_pre>0);
			if(loc_pre[index] > 0)
			{
				temp_ind[gtz_ind] = index;
				gtz_ind++;
			}
		}
						
		int[] ind = new int[gtz_ind];
		System.arraycopy(temp_ind, 0, ind, 0, gtz_ind);
		
		double[] loc_pre_new = new double[gtz_ind];
		for(int index = 0; index < loc_pre_new.length; index++)
		{
			loc_pre_new[index] = loc_pre[ind[index]];
		}
		
		
		double temp_pre, temp_current;
		for(int i = 1; i <= loc_pre_new.length; i++)
		{
			for(int j = 1; j <= loc_current.length; j++)
			{
				if(Math.abs(loc_pre_new[i-1] - loc_current[j-1]) > 0  && Math.abs(loc_pre_new[i-1] - loc_current[j-1]) < 2*Pd)
				{
					temp_pre = sig[(int) (loc_pre_new[i - 1] - 1)];
					temp_current = sig[(int) (loc_current[j-1]) -1];
					if(temp_pre > temp_current)
					{
						invalid = loc_current[j-1];
					}else if(temp_pre < temp_current)
					{
						invalid = loc_pre_new[i-1];
					}
				}
			}
		}
				
		pc_G_false = invalid+start-1;
		return pc_G_false;
	}
	
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// %%%%%%%%%%%  Helper Functions %%%%%%%%%%%%%%%%%%%
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	//앞 좌표와의 차이를 계산함
	public static double[] diff(double[] inArray)
	{
		double[] result = new double[inArray.length - 1];
		for(int i = 0;i < result.length; i++)
		{
			result[i] = inArray[i+1] - inArray[i];
		}
		return result;
	}
	
	public static double[] fix(double[] inArray)
	{
		double[] result = new double[inArray.length];
		for(int ind = 0; ind < inArray.length; ind++)
		{
			result[ind] = Math.round(inArray[ind]);
		}
		
		return result;
	}
	
	public static double max(double[] inArray)
	{
		if(inArray.length < 1)
		{
			return 0;
		}		
		double max_val = inArray[0];
		for(int i = 0;i < inArray.length; i++)
		{
			if(inArray[i] > max_val)
				max_val = inArray[i];
		}
		return max_val;
	}
	
	public static int maxInd(double[] inArray)
	{
		if(inArray.length < 1)
		{
			return 0;
		}		
		double max_val = inArray[0];
		int max_ind = 0;
		for(int i = 0;i < inArray.length; i++)
		{
			if(inArray[i] > max_val)
			{
				max_val = inArray[i];
				max_ind = i;
			}
		}
		return max_ind;
	}
	
	public static double[] abs(double[] inArray)
	{
		double[] result = new double[inArray.length];
		for(int i = 0;i < result.length; i++)
		{
			result[i] = Math.abs(inArray[i]);
		}
		return result;
	}
	
	public static double median(double[] inArray)
	{
		int len = inArray.length;
		double[] result = new double[len];
		System.arraycopy(inArray, 0, result, 0, len);
		double med;
		Arrays.sort(result); //오름차순 정렬
		if(len % 2 == 0) //배열의 갯수가 짝수면
		{
			med = (result[(len/2)-1] + result[(len)/2])/2;
		}else
		{
			med = result [(len-1)/2];
		}
		return med;
	}
	
	public static double arraySum(double[] in, int start, int stop)
	{
		double result = 0;
		for(int index = start; index < stop; index++)
		{
			result = result + in[index];
		}
		return result;
	}
	
	// Converts an array of floats to an array of complex numbers
	 public static Complex[] arrayD2C(double[] x)
	    {
	    	Complex[] result = new Complex[x.length];
	    	for (int i = 0; i < x.length; i++)
	    	{
	    		result[i] = new Complex(x[i],0);
	    	}
	    	return result;
	    }
	 
	 // Converts an array of complex numbers to float, using abs value.
	 public static double[] arrayC2D(Complex[] input)
	    {
	    	double[] result = new double[input.length];
	    	for (int i = 0; i < input.length; i++)
	    	{
	    		result[i] = (double) input[i].abs();
	    	}
	    	return result;
	    }

}
