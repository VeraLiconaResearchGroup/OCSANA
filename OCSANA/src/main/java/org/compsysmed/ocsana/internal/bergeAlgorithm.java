/*
 * Copyright (C) 2015 misagh.kordi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.compsysmed.ocsana.internal;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author misagh.kordi
 */
public class bergeAlgorithm {

    private final int maxCIsize;
    
    private final double maxNumberofCandidate;

    private final Vector<Vector<Integer>> elementarypathes;

    private Vector<Vector<Integer>> CIset;

    private final Vector<Integer> elementaryNodes;

    private final Vector<Float> ocsanaScore;

    float[] ocsanaScoreofCISet;

    
    /**
     * Constructor
     * Initial value of  maxCIsize, maxNumberofCandidate, elementarypathes, elementarypathes, ocsanaScore
     *
     * @param maxCIsize, maxNumberofCandidate, elementarypathes, elementarypathes, ocsanaScore.
     */
    public bergeAlgorithm(int maxCIsize, double maxNumberofCandidate , Vector<Vector<Integer>> elementarypathes,  Vector<Integer> elementaryNodes, Vector<Float> ocsanaScore) {
        this.maxCIsize = maxCIsize;
        this.elementarypathes = elementarypathes;      
        this.elementaryNodes = elementaryNodes;
        this.ocsanaScore = ocsanaScore;
        this.maxNumberofCandidate =  maxNumberofCandidate;
        initial();
    }

    
    
    /**
     * Initial result1 vector and 
     * call setElementarypathes to find all Elementary paths and
     * call Berge function to find solution and
     * call sort function to sort out put based on their Ocsana score 
     *
     * @param 
     */
    private void initial() {

        CIset = new Vector<>();
        Vector<Integer> result1 = new Vector<>();
        BergeAlgorithm(1, result1);
        sort();
    }

    
    
    /**
     * @return ocsana score of given path 
     *
     * @param Path, one elementary path 
     */
    public float setPathScore(Vector<Integer> Path) {

        float CIscore = 0;

        int temp = 0;

        for (int i = 0; i < Path.size()-1; i++) {

            temp = elementaryNodes.indexOf(Path.get(i));
            
           
            CIscore = CIscore + ocsanaScore.get(temp);
        }
        return CIscore;
    }
    
    /**
     * recursive function that add one node from elementary nodes to cover i-th path
     * and call itself for next elementary path
     *
     * @param i that is index of i-th path from elementary paths and 
     * currenttemp, current chosen elements as solution
     */
    private void BergeAlgorithm(int i, Vector<Integer> currenttemp) {

        if(CIset.size()==maxNumberofCandidate || currenttemp.size()>maxCIsize )
            return;
        
        Vector<Integer> current = new Vector<>();
        for (int j = 0; j < currenttemp.size(); j++) {
            current.addElement(currenttemp.get(j));
        }
        if (i > elementarypathes.size() ) {
           
            addtoresult(current);
            return;
            
        } else {
            
            while (cover(current, elementarypathes.get(i - 1)) == true) {
                i++;
                if (i > elementarypathes.size()) {
                  
                    addtoresult(current);
                    return;
                }
            }
            Vector<Integer> temp = elementarypathes.get(i - 1);
                
            for (int j = 1; j < temp.size()-1 || (j < temp.size() && elementaryNodes.contains(temp.get(temp.size()-1)) ) ; j++) {
              
                    current.addElement(temp.get(j));
                    int x = 0;
                    
                    for (int k = 0; k < CIset.size(); k++) {
                        if (comapringtwopath(CIset.get(k), current)) {

                            x++;
                            break;
                        }
                    }
                    if (x == 0) 
                        BergeAlgorithm(i + 1, current);
                       
                    current.remove(current.size() - 1);
            }
        }
    }

    /**
     * add as solution if current is acceptable solution for hitting set, based on current solution
     * and call itself for next elementary path
     *
     * @param current, one possible solution for hitting set
     */
    private void addtoresult(Vector<Integer> current) {

        Vector<Integer> currenttemp = new Vector<>();
        currenttemp = current;
        
        CIset.addElement(currenttemp);
        for (int j = 0; j < CIset.size() - 1; j++) {
            
            if (comapringtwopath(CIset.get(CIset.size() - 1), CIset.get(j)) || comapringtwopath(CIset.get(j), CIset.get(CIset.size() - 1))) {
                
                if (CIset.get(j).size() < CIset.get(CIset.size() - 1).size()) {   
                    
                    CIset.remove(CIset.size() - 1);
                    
                } else {
                    CIset.remove(j);
                }
            }
        }  
    }

    /**
     * check whether path1 is subset of path2 or not
     * @return true iff path1 is subset of path2 and false,  if it is not
     *
     * @param two paths Path1 and Path2
     */
    private boolean comapringtwopath(Vector<Integer> Path1, Vector<Integer> Path2) {
        boolean equal = true;

        int temp = 0;

        for (int i = 0; i < Path1.size(); i++) {

            if (Path2.contains(Path1.get(i))) {
                
                temp++;
            }
        }

        if (temp != Path1.size()) {
            equal = false;
        }
        return equal;
    }

    /**
     * check whether current has at least one element of  path1 or not 
     * @return true iff current has at least one element of  path1 and false,  if it is not
     *
     * @param two paths Path1 and current
     */
    public boolean cover(Vector<Integer> current, Vector<Integer> Path1) {

        boolean cover = false;

        for (int i = 0; i < current.size(); i++) {

            if (Path1.contains(current.get(i))) {
                
                cover = true;
                break;
            }
        }
        return cover;
    }

    /**
     * sort result based on their ocsana score
     * 
     *
     * @param 
     */
    public void sort() {//sort in decreasing order not increasing 
        int count = 0;
        ocsanaScoreofCISet = new float[CIset.size()];
         
        for (int i = 0; i < CIset.size(); i++) {
            
            ocsanaScoreofCISet[count++] = setPathScore(CIset.get(i));
            
        }
        int length = ocsanaScoreofCISet.length;
        
        quickSort(0, length - 1);
    }

    /**
     * quickSort algorithm
     * 
     *
     * @param 
     */
    private void quickSort(int lowerIndex, int higherIndex) {

        int i = lowerIndex;
        int j = higherIndex;
     
        Float pivot = ocsanaScoreofCISet[lowerIndex + (higherIndex - lowerIndex) / 2];
      
        while (i <= j) {
           
            while (ocsanaScoreofCISet[i] < pivot) {
                i++;
            }
            while (ocsanaScoreofCISet[j] > pivot) {
                j--;
            }
            if (i <= j) {

                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j) {
            quickSort(lowerIndex, j);
        }
        if (i < higherIndex) {
            quickSort(i, higherIndex);
        }
    }

    /**
     * exchange two element on quickSort algorithm
     * 
     *
     * @param 
     */
    private void exchangeNumbers(int i, int j) {

        Float temp = ocsanaScoreofCISet[i];
        Vector<Integer> tempvec;
        tempvec = CIset.get(i);

        if (i < j) {

            ocsanaScoreofCISet[i] = ocsanaScoreofCISet[j];
            ocsanaScoreofCISet[j] = temp;

            CIset.set(i, CIset.get(j));
            CIset.set(j, tempvec);

        }
    }

    /**
     * return CIset as solution for hitting set problem based on Berge Algorithm
     * 
     *
     * @param 
     */
    public Vector<Vector<Integer>> getResult(){
    
        return CIset;
    }

}
