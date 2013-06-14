package graph.algorithms;


import java.lang.Math.*;
import java.util.Random;

public class GraphTheoryAlgorithms extends Object {

public static void allCliques(int n, int m, int nodei[], int nodej[],
                              int clique[][])
{
  int i,j,k,level,depth,num,numcliques,small,nodeu,nodev,nodew=0;
  int sum,p,up,low,index1,index2,indexv=0;
  int currentclique[] = new int[n+1];
  int aux1[] = new int[n+1];
  int aux2[] = new int[n+1];
  int notused[] = new int[n+2];
  int used[] = new int[n+2];
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+m+1];
  int stack[][] = new int[n+2][n+2];
  boolean join,skip,hop;

  // set up the forward star representation of the graph
  k = 0;
  for (i=1; i<=n; i++) {
    firstedges[i] = k + 1;
    for (j=1; j<=m; j++) {
      if (nodei[j] == i) {
        k++;
        endnode[k] = nodej[j];
      }
      if (nodej[j] == i) {
        k++;
        endnode[k] = nodei[j];
      }
    }
  }
  firstedges[n+1] = k + 1;
  level = 1;
  depth = 2;
  for (i=1; i<=n; i++)
    stack[level][i] = i;
  numcliques = 0;
  num = 0;
  used[level] = 0;
  notused[level] = n;
  while (true) {
    small = notused[level];
    nodeu = 0;
    aux1[level] = 0;
    while (true) {
      nodeu++;
      if ((nodeu > notused[level]) || (small == 0)) break;
      index1 = stack[level][nodeu];
      sum = 0;
      nodev = used[level];
      while (true) {
        nodev++;
        if ((nodev > notused[level]) || (sum >= small)) break;
        p = stack[level][nodev];
        if (p == index1)
          join = true;
        else {
          join = false;
          low = firstedges[p];
          up = firstedges[p + 1];
          if (up > low) {
            up--;
            for (k=low; k<=up; k++)
              if (endnode[k] == index1) {
                join = true;
                break;
              }
          }
        }
        // store up the potential candidate
        if (!join) {
          sum++;
          indexv = nodev;
        }
      }
      if (sum < small) {
        aux2[level] = index1;
        small = sum;
        if (nodeu <= used[level])
          nodew = indexv;
        else {
          nodew = nodeu;
          aux1[level] = 1;
        }
      }
    }
    // backtrack
    aux1[level] += small;
    while (true) {
      hop = false;
      if (aux1[level] <= 0) {
        if (level <= 1) return; 
        level--;
        depth--;
        hop = true;
      }
      if (!hop) {
        index1 = stack[level][nodew];
        stack[level][nodew] = stack[level][used[level]+1];
        stack[level][used[level]+1] = index1;
        index2 = index1;
        nodeu = 0;
        used[depth] = 0;
        while (true) {
          nodeu++;
          if (nodeu > used[level]) break;
          p = stack[level][nodeu];
          if (p == index2)
             join = true;
          else {
            join = false;
            low = firstedges[p];
            up = firstedges[p + 1];
            if (up > low) {
               up--;
               for (k=low; k<=up; k++)
                 if (endnode[k] == index2) {
                   join = true;
                   break;
                 }
            }
          }
          if (join) {
            used[depth]++;
            stack[depth][used[depth]] = stack[level][nodeu];
          }
        }
        notused[depth] = used[depth];
        nodeu = used[level] + 1;
        while (true) {
          nodeu++;
          if (nodeu > notused[level]) break;
          p = stack[level][nodeu];
          if (p == index2)
            join = true;
          else {
            join = false;
            low = firstedges[p];
            up = firstedges[p + 1];
            if (up > low) {
              up--;
              for (k=low; k<=up; k++)
                if (endnode[k] == index2) {
                  join = true;
                  break;
                }
            }
          }
          if (join) {
            notused[depth]++;
            stack[depth][notused[depth]] = stack[level][nodeu];
          }
        }
        num++;
        currentclique[num] = index2;
        if (notused[depth] == 0) {
          // found a clique
          numcliques++;
          clique[numcliques][0] = num;
          for (i=1; i<=num; i++)
            clique[numcliques][i] = currentclique[i];
          clique[0][0] = numcliques;
        }
        else {
          if (used[depth] < notused[depth]) {
            level++;
            depth++;
            break;
          }
        }
      }
      while (true) {
        num--;
        used[level]++;
        if (aux1[level] > 1) {
          nodew = used[level];
          // look for candidate
          while (true) {
            nodew++;
            p = stack[level][nodew];
            if (p == aux2[level]) continue;
            low = firstedges[p];
            up = firstedges[p + 1];
            if (up <= low) break;
            up--;
            skip = false;
            for (k=low; k<=up; k++)
              if (endnode[k] == aux2[level]) {
                skip = true;
                break;
              }
            if (!skip) break;
          }
        }
        aux1[level]--;
        break;
      }
    }
  }
}


public static void allShortestPathLength(int n, int m, int nodei[],
                     int nodej[], boolean directed[], int weight[], 
                     int root, int mindistance[])
{
  int i,j,k,n2,large,nodeu,nodev,minlen,temp,minj=0,minv=0;
  int location[] = new int[n+1];
  int distance[][] = new int[n+1][n+1];

  // obtain a large number greater than all edge weights
  large = 1;
  for (k=1; k<=m; k++)
    large += weight[k];

  // set up the distance matrix
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      distance[i][j] = (i == j) ? 0 : large;
  for (k=1; k<=m; k++) {
    i = nodei[k];
    j = nodej[k];
    if (directed[k])
      distance[i][j] = weight[k];
    else
      distance[i][j] = distance[j][i] = weight[k];
  }
      
  if (root != 1) {
    // interchange rows 1 and root
    for (i=1; i<=n; i++) {
      temp = distance[1][i];
      distance[1][i] = distance[root][i];
      distance[root][i] = temp;
    }
    // interchange columns 1 and root
    for (i=1; i<=n; i++) {
      temp = distance[i][1];
      distance[i][1] = distance[i][root];
      distance[i][root] = temp;
    }
  }
  nodeu = 1;
  n2 = n + 2;
  for (i=1; i<=n; i++) {
    location[i] = i;
    mindistance[i] = distance[nodeu][i];
  }
  for (i=2; i<=n; i++) {
    k = n2 - i;
    minlen = large;
    for (j=2; j<=k; j++) {
      nodev = location[j];
      temp = mindistance[nodeu] + distance[nodeu][nodev];
      if (temp < mindistance[nodev]) mindistance[nodev] = temp;
      if (minlen > mindistance[nodev]) {
        minlen = mindistance[nodev];
        minv = nodev;
        minj = j;
      }
    }
    nodeu = minv;
    location[minj] = location[k];
  }
  if (root != 1) {
    mindistance[1] = mindistance[root];
    mindistance[root] = 0;
    // interchange rows 1 and root
    for (i=1; i<=n; i++) {
      temp = distance[1][i];
      distance[1][i] = distance[root][i];
      distance[root][i] = temp;
    }
    // interchange columns 1 and root
    for (i=1; i<=n; i++) {
      temp = distance[i][1];
      distance[i][1] = distance[i][root];
      distance[i][root] = temp;
    }
  }
}


public static void allPairsShortestPaths(int n, int dist[][], int big,
                                int startnode, int endnode, int path[])
{
  int i,j,k,d,num,node;
  int next[][] = new int[n+1][n+1];
  int order[] = new int[n+1];

  // compute the shortest path distance matrix
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      next[i][j] = i;
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      if (dist[j][i] < big)
        for (k=1; k<=n; k++)
          if (dist[i][k] < big) {
            d = dist[j][i] + dist[i][k];
            if (d < dist[j][k]) {
               dist[j][k] = d;
               next[j][k] = next[i][k];
            }
          }
  // find the shortest path from startnode to endnode
  if (startnode == 0) return;
  j = endnode;
  num = 1;
  order[num] = endnode;
  while (true) {
    node = next[startnode][j];
    num++;
    order[num] = node;
    if (node == startnode) break;
    j = node;
  }
  for (i=1; i<=num; i++)
    path[i] = order[num-i+1];
  path[0] = num;
}


public static void assignment(int n, int cost[][], int sol[])
{
  int h, i, j, k, tmpcol, tmprow, m, optcost, n1, temp;
  int tmpsetcol=0, tmpsetrow=0, r=0, l=0;
  int unexplorecol[] = new int[n + 1];
  int labelcol[] = new int[n + 1];
  int labelrow[] = new int[n + 1];
  int lastunassignrow[] = new int[n + 1];
  int nextunassignrow[] = new int[n + 1];
  int setlabelcol[] = new int[n + 1];
  int setlabelrow[] = new int[n + 1];
  int unexplorerow[] = new int[n + 2];
  int unassginrow[] = new int[n + 2];
  boolean skip=false, outer, middle;

  // initialize
  n1 = n + 1;
  for (j=1; j<=n; j++) {
    sol[j] = 0;
    lastunassignrow[j] = 0;
    nextunassignrow[j] = 0;
    unassginrow[j] = 0;
  }
  unassginrow[n1] = 0;
  optcost = 0;

  // cost matrix reduction
  for (j=1; j<=n; j++) {
    temp = cost[1][j];
    for (l=2; l<=n; l++)
      if (cost[l][j] < temp) temp = cost[l][j];
    optcost += temp;
    for (i=1; i<=n; i++)
      cost[i][j] -= temp;
  }
  for (i=1; i<=n; i++) {
    temp = cost[i][1];
    for (l=2; l<=n; l++)
      if (cost[i][l] < temp) temp = cost[i][l];
    optcost += temp;
    l = n1;
    for (j=1; j<=n; j++) {
      cost[i][j] -= temp;
      if ( cost[i][j] == 0 ) {
        cost[i][l] = -j;
        l = j;
      }
    }
  }

  // choosing initial solution
  k = n1;
  for (i=1; i<=n; i++) {
    tmpcol = n1;
    j = -cost[i][n1];
    skip = false;
    do {
      if ( sol[j] == 0 ) {
        skip = true;
        break;
      }
      tmpcol = j;
      j = -cost[i][j];
    } while ( j != 0 );
    if (skip) {
      sol[j] = i;
      cost[i][tmpcol] = cost[i][j];
      nextunassignrow[i] = -cost[i][j];
      lastunassignrow[i] = tmpcol;
      cost[i][j] = 0;
      continue;
    }
    tmpcol = n1;
    j = -cost[i][n1];
    do {
      r = sol[j];
      tmprow = lastunassignrow[r];
      m = nextunassignrow[r];
      while (true) {
        if (m == 0) break;
        if (sol[m] == 0) {
          nextunassignrow[r] = -cost[r][m];
          lastunassignrow[r] = j;
          cost[r][tmprow] = -j;
          cost[r][j] = cost[r][m];
          cost[r][m] = 0;
          sol[m] = r;
          sol[j] = i;
          cost[i][tmpcol] = cost[i][j];
          nextunassignrow[i] = -cost[i][j];
          lastunassignrow[i] = tmpcol;
          cost[i][j] = 0;
          skip = true;
          break;
        }
        tmprow = m;
        m = -cost[r][m];
      }
      if (skip) break;
      tmpcol = j;
      j = -cost[i][j];
    } while ( j != 0 );
    if (skip) continue;
    unassginrow[k] = i;
    k = i;
  }
  middle = false;
  while (true) {
    outer = false;
    if (!middle) {
      if (unassginrow[n1] == 0) {
      	cost[0][0] = optcost;
      	return;
      }

      // search for a new assignment
      for (i=1; i<=n; i++) {
        unexplorecol[i] = 0;
        labelcol[i] = 0;
        labelrow[i] = 0;
        unexplorerow[i] = 0;
      }
      unexplorerow[n1] = -1;
      tmpsetcol = 0;
      tmpsetrow = 1;
      r = unassginrow[n1];
      labelrow[r] = -1;
      setlabelrow[1] = r;
    }
    if ((cost[r][n1] != 0) || middle) {
      do {
        if (!middle) {
          l = -cost[r][n1];
          if ( cost[r][l] != 0 ) {
            if ( unexplorerow[r] == 0 ) {
              unexplorerow[r] = unexplorerow[n1];
              unexplorecol[r] = -cost[r][l];
              unexplorerow[n1] = r;
            }
          }
          skip = false;
        }
        while (true) {
          if (!middle) {
            if (labelcol[l] == 0) break;
            if (unexplorerow[r] == 0) {
              skip = true;
              break;
            }
          }
          middle = false;
          l = unexplorecol[r];
          unexplorecol[r] = -cost[r][l];
          if ( cost[r][l] == 0 ) {
            unexplorerow[n1] = unexplorerow[r];
            unexplorerow[r] = 0;
          }
        }
        if (skip) break;
        labelcol[l] = r;
        if ( sol[l] == 0 ) {
          while (true) {
            // assigning a new row
            sol[l] = r;
            m = n1;
            while (true) {
              temp = -cost[r][m];
              if (temp == l) break;
              m = temp;
            }
            cost[r][m] = cost[r][l];
            cost[r][l] = 0;
            if (labelrow[r] < 0) break;
            l = labelrow[r];
            cost[r][l] = cost[r][n1];
            cost[r][n1] = -l;
            r = labelcol[l];
          }
          unassginrow[n1] = unassginrow[r];
          unassginrow[r] = 0;
          outer = true;
          break;
        }
        if (outer) break;
        tmpsetcol++;
        setlabelcol[tmpsetcol] = l;
        r = sol[l];
        labelrow[r] = l;
        tmpsetrow++;
        setlabelrow[tmpsetrow] = r;
      } while (cost[r][n1] != 0);
      if (outer) continue;
      if ( unexplorerow[n1] > 0 ) middle = true;
    }
    if (!middle) {
      // current cost matrix reduction
      h = Integer.MAX_VALUE;
      for (j=1; j<=n; j++)
        if ( labelcol[j] == 0 )
          for (k=1; k<=tmpsetrow; k++) {
            i = setlabelrow[k];
            if (cost[i][j] < h) h = cost[i][j];
          }
      optcost += h;
      for (j=1; j<=n; j++)
        if (labelcol[j] == 0)
          for (k=1; k<=tmpsetrow; k++) {
            i = setlabelrow[k];
            cost[i][j] -= h;
            if ( cost[i][j] == 0 ) {
              if ( unexplorerow[i] == 0 ) {
                unexplorerow[i] = unexplorerow[n1];
                unexplorecol[i] = j;
                unexplorerow[n1] = i;
              }
              l = n1;
              while (true) {
                temp = -cost[i][l];
                if (temp == 0) break;
                l = temp;
              }
              cost[i][l] = -j;
            }
          }
      if ( tmpsetcol != 0 )
        for (i=1; i<=n; i++) {
          if ( labelrow[i] == 0 ) {
            for (k=1; k<=tmpsetcol; k++) {
              j = setlabelcol[k];
              if ( cost[i][j] <= 0 ) {
                l = n1;
                while (true) {
                  temp = - cost[i][l];
                  if (temp == j) break;
                  l = temp;
                }
                cost[i][l] = cost[i][j];
                cost[i][j] = h;
                continue;
              }
              cost[i][j] += h;
            }
          }
      }
    }
    r = unexplorerow[n1];
    middle = true;
  }
}


public static void bottleneckAssignment(int n, int cost[][], int sol[])
{
  int i, minimum, bottleneckvalue, cand, p, v, w, start, temp;
  int q=0, u=0;
  int aux1[] = new int[n + 1];
  int aux2[] = new int[n + 1];
  int aux3[] = new int[n + 1];
  int aux4[] = new int[n + 1];
  boolean skip=false;

  // initialize
  bottleneckvalue = -Integer.MAX_VALUE;
  for (i=1; i<=n; i++) 
    aux1[i]=0;
  for (i=0; i<=n-1; i++) {
    q = n - i;
    aux2[q] = q;
    minimum = Integer.MAX_VALUE;
    p = 1;
    do {
      cand = cost[q][p];
      if (cand <= bottleneckvalue) {
        minimum = bottleneckvalue;
        u = p;
        p = n + 1;
      }
      else {
        if (cand < minimum) {
          minimum = cand;
          u = p;
        }
        p++;
      }
    } while (p <= n);
    if (aux1[u] == 0) {
      aux1[u] = q;
      sol[q] = u;
    }
    else
      sol[q] = 0;
    if (minimum > bottleneckvalue) bottleneckvalue = minimum;
  }
  // search for an augmenting path
  for (u=1; u<=n; u++)
    if (aux1[u] == 0) {
      w = 1;
      start = 1;
      skip = false;
      for (i=1; i<=n; i++) {
        q = aux2[i];
        aux4[q] = cost[q][u];
        aux3[q] = u;
        if (aux4[q] <= bottleneckvalue) {
          if (sol[q] == 0) {
            skip = true;
            break;
          }
          aux2[i] = aux2[w];
          aux2[w] = q;
          w++;
        }
      }
      if (!skip) {
        while (true) {
          if (w == start) {
            minimum = Integer.MAX_VALUE;
            for (i=w; i<=n; i++) {
              q = aux2[i];
              cand = aux4[q];
              if (cand <= minimum) {
                if (cand < minimum) {
                  w = start;
                  minimum = cand;
                }
                aux2[i] = aux2[w];
                aux2[w] = q;
                w++;
              }
            }
            bottleneckvalue = minimum;
            for (i=start; i<=w-1; i++) {
              q = aux2[i];
              if (sol[q] == 0) {
                skip = true;
                break;
              }
            }
          }
          if (skip) break;
          v = aux2[start];
          start++;
          p = sol[v];
          for (i=w; i<=n; i++) {
            q = aux2[i];
            cand = cost[q][p];
            if (cand < aux4[q]) {
              aux3[q] = p;
              if (cand <= bottleneckvalue) {
                if (sol[q] == 0) {
                  skip = true;
                  break;
                }
                aux2[i] = aux2[w];
                aux2[w] = q;
                w++;
              }
              aux4[q] = cand;
            }
          }
          if (skip) break; 
        }
      }
      // augment
      skip = false;
      do {
        p = aux3[q];
        sol[q] = p;
        temp = q;
        q = aux1[p];
        aux1[p] = temp;
      } while (p != u);
    }
  cost[0][0] = bottleneckvalue;
}


public static void breadthFirstSearch(int n, int m, int nodei[],
                      int nodej[], int parent[], int sequence[])
{
  int i,j,k,enqueue,dequeue,queuelength,p,q,u,v;
  int queue[] = new int[n+1];
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+1];
  boolean mark[] = new boolean[m+1];
  boolean iterate,found;

  // set up the forward star representation of the graph
  for (j=1; j<=m; j++)
    mark[j] = true;
  firstedges[1] = 0;
  k = 0;
  for (i=1; i<=n; i++) {
    for (j=1; j<=m; j++)
      if (mark[j]) {
        if (nodei[j] == i) {
          k++;
          endnode[k] = nodej[j];
          mark[j] = false;
        }
        else {
          if (nodej[j] == i) {
            k++;
            endnode[k] = nodei[j];
            mark[j] = false;
          }
        }
      }
    firstedges[i+1] = k;
  }
  for (i=1; i<=n; i++) {
    sequence[i] = 0;
    parent[i] = 0;
  }
  k = 0;
  p = 1;
  enqueue = 1;
  dequeue = 1;
  queuelength = enqueue;
  queue[enqueue] = p;
  k++;
  sequence[p] = k;
  parent[p] = 0;
  iterate = true;
  // store all descendants
  while (iterate) {
    for (q=1; q<=n; q++) {
      // check if p and q are adjacent
      if (p < q) {
        u = p;
        v = q;
      }
      else {
        u = q;
        v = p;
      }
      found = false;
      for (i=firstedges[u]+1; i<=firstedges[u+1]; i++)
        if (endnode[i] == v) {
          // p and q are adjacent
          found = true;
          break;
        }
      if (found && sequence[q] == 0) {
        enqueue++;
        if (n < enqueue) enqueue = 1;
        queue[enqueue] = q;
        k++;
        parent[q] = p;
        sequence[q] = k;
      }
    }
    // process all nodes of the same height
    if (enqueue >= dequeue) {
      if (dequeue == queuelength) {
        queuelength = enqueue;
      }
      p = queue[dequeue];
      dequeue++;
      if (n < dequeue) dequeue = 1;
      iterate = true;
      // process other components
    }
    else {
      iterate = false;
      for (i=1; i<=n; i++)
        if (sequence[i] == 0) {
          dequeue = 1;
          enqueue = 1;
          queue[enqueue] = i;
          queuelength = 1;
          k++;
          sequence[i] = k;
          parent[i] = 0;
          p = i;
          iterate = true;
          break;
        }
    }
  }
}


public static void cardinalityMatching(int n, int m, int nodei[],
                                       int nodej[], int pair[])
{
  int i, j, k, n1, istart, first, last, nodep, nodeq, nodeu, nodev, nodew;
  int neigh1, neigh2, unmatch;
  int fwdedge[] = new int[m + m + 1];
  int firstedge[] = new int[n + 2];
  int grandparent[] = new int[n + 1];
  int queue[] = new int[n + 1];
  boolean outree[] = new boolean[n + 1];
  boolean newnode, nopath;

  // set up the forward star graph representation
  n1 = n + 1;
  k = 0;
  for (i=1; i<=n; i++) { 
    firstedge[i] = k + 1;
    for (j=1; j<=m; j++) 
      if (nodei[j] == i) {
        k++;
        fwdedge[k] = nodej[j];
      }
      else {
        if (nodej[j] == i) {
          k++;
          fwdedge[k] = nodei[j];
        }
      }
  }
  firstedge[n1] = m + 1;

  // all nodes are unmatched
  unmatch = n;
  for (i=1; i<=n; i++) 
    pair[i] = 0;
  for (i=1; i<=n; i++)
    if (pair[i] == 0) {
      j = firstedge[i];
      k = firstedge[i + 1] - 1;
      while ((pair[fwdedge[j]] != 0) && (j < k))
        j++;
      if (pair[fwdedge[j]] == 0) {
        // match a pair of nodes
        pair[fwdedge[j]] = i;
        pair[i] = fwdedge[j];
        unmatch -= 2;
      }
    }
  for (istart=1; istart<=n; istart++) 
    if ((unmatch >= 2) && (pair[istart] == 0)) {
      // 'istart' is not yet matched
      for (i=1; i<=n; i++)
        outree[i] = true;
      outree[istart] = false;
      // insert the root in the queue
      queue[1] = istart;
      first = 1;
      last = 1;
      nopath = true;
      do {
        nodep = queue[first];
        first = first + 1;
        nodeu = firstedge[nodep];
        nodew = firstedge[nodep + 1] - 1;
        while (nopath && (nodeu <= nodew)) {
          // examine the neighbor of 'nodep'
          if (outree[fwdedge[nodeu]]) {
            neigh2 = fwdedge[nodeu];
            nodeq = pair[neigh2];
            if (nodeq == 0) {
              // an augmentation path is found
              pair[neigh2] = nodep;
              do {
                neigh1 = pair[nodep];
                pair[nodep] = neigh2;
                if (neigh1 != 0) {
                  nodep = grandparent[nodep];
                  pair[neigh1] = nodep;
                  neigh2 = neigh1;
                }
              } while (neigh1 != 0);
              unmatch -= 2;
              nopath = false;
            }
            else {
              if (nodeq != nodep) {
                if (nodep == istart)
                   newnode = true;
                else {
                  nodev = grandparent[nodep];
                  while ((nodev != istart) && (nodev != neigh2))
                     nodev = grandparent[nodev];
                  newnode = (nodev == istart ? true : false);
                }
                if (newnode) {
                  // add a tree link
                  outree[neigh2] = false;
                  grandparent[nodeq] = nodep;
                  last++;
                  queue[last] = nodeq;
                }
              }
            }
          }
          nodeu++;
        }
      } while (nopath && (first <= last));
    }
  pair[0] = unmatch;
}


public static void ChinesePostmanTour(int n, int m, int startnode,
                                      int nodei[], int nodej[], int cost[],
                                      int sol[][], int trail[])
{
  int i,iplus1,j,k,idxa,idxb,idxc,idxd,idxe,wt,high,duparcs,totsolcost;
  int loch,loca,locb,locc,locd,loce,locf,locg,hub,tmpopty,tmpoptx=0;
  int nplus,p,q,cur,curnext,position=0;
  int neighbor[] = new int[m + m + 1];
  int weight[] = new int[m + m + 1];
  int degree[] = new int[n + 1];
  int next[] = new int[n + 2];
  int core[] = new int[n + 1];
  int aux1[] = new int[n + 1];
  int aux2[] = new int[n + 1];
  int aux3[] = new int[n + 1];
  int aux4[] = new int[n + 1];
  int aux5[] = new int[n + 1];
  int aux6[] = new int[n + 1];
  int tmparg[] = new int[1];
  float wk1[] = new float[n + 1];
  float wk2[] = new float[n + 1];
  float wk3[] = new float[n + 1];
  float wk4[] = new float[n + 1];
  float eps,work1,work2,work3,work4;
  boolean skip,complete;

  eps = 0.0001f;
  // check for connectedness
  if (!connected(n,m,nodei,nodej)) {
    sol[0][0] = 1;
    return;
  }
  sol[0][0] = 0;

  // store up the neighbors of each node
  for (i=1; i<=n; i++)
    degree[i] = 0;
  for (j=1; j<=m; j++) {
    degree[nodei[j]]++;
    degree[nodej[j]]++;
  }
  next[1] = 1;
  for (i=1; i<=n; i++) {
    iplus1 = i + 1;
    next[iplus1] = next[i] + degree[i];
    degree[i] = 0;
  }
  totsolcost = 0;
  high = 0;
  for (j=1; j<=m; j++) {
    totsolcost += cost[j];
    k = next[nodei[j]] + degree[nodei[j]];
    neighbor[k] = nodej[j];
    weight[k] = cost[j];
    degree[nodei[j]]++;
    k = next[nodej[j]] + degree[nodej[j]];
    neighbor[k] = nodei[j];
    weight[k] = cost[j];
    degree[nodej[j]]++;
    high += cost[j];
  }
  nplus = n + 1;
  locg = -nplus;
  for (i=1; i<=n; i++)
    wk4[i] = high;
  // initialization
  for (p=1; p<=n; p++) {
    core[p] = p;
    aux1[p] = p;
    aux4[p] = locg;
    aux5[p] = 0;
    aux3[p] = p;
    wk1[p] = 0f;
    wk2[p] = 0f;
    i = next[p];
    loch = next[p+1];
    loca = loch - i;
    locd = loca / 2;
    locd *= 2;
    if (loca != locd) {
      loch--;
      aux4[p] = 0;
      wk3[p] = 0f;
      for (q=i; q<=loch; q++) {
        idxc = neighbor[q];
        work2 = (float) (weight[q]);
        if (wk4[idxc] > work2) {
          aux2[idxc] = p;
          wk4[idxc] = work2;
        }
      }
    }
  }
  // examine the labeling
  iterate:
  while (true) {
    work1 = high;
    for (locd=1; locd<=n; locd++)
      if (core[locd] == locd) {
        work2 = wk4[locd];
        if (aux4[locd] >= 0) {
          work2 = 0.5f * (work2 + wk3[locd]);
          if (work1 >= work2) {
            work1 = work2;
            tmpoptx = locd;
          }
        }
        else {
          if (aux5[locd] > 0) work2 += wk1[locd];
          if (work1 > work2) {
            work1 = work2;
            tmpoptx = locd;
          }
        }
      }
    work4 = ((float)high) / 2f;
    if (work1 >= work4) {
      sol[0][0] = 2;
      return;
    }
    if (aux4[tmpoptx] >= 0) {
      idxb = aux2[tmpoptx];
      idxc = aux3[tmpoptx];
      loca = core[idxb];
      locd = tmpoptx;
      loce = loca;
      while (true) {
        aux5[locd] = loce;
        idxa = aux4[locd];
        if (idxa == 0) break;
        loce = core[idxa];
        idxa = aux5[loce];
        locd = core[idxa];
      }
      hub = locd;
      locd = loca;
      loce = tmpoptx;
      while (true) {
        if (aux5[locd] > 0) break;
        aux5[locd] = loce;
        idxa = aux4[locd];
        if (idxa == 0) {
          // augmentation
          loch = 0;
          for (locb=1; locb<=n; locb++)
            if (core[locb] == locb) {
              idxd = aux4[locb];
              if (idxd >= 0) {
                if (idxd == 0) loch++;
                work2 = work1 - wk3[locb];
                wk3[locb] = 0f;
                wk1[locb] += work2;
                aux4[locb] = -idxd;
              }
              else {
                idxd = aux5[locb];
                if (idxd > 0) {
                  work2 = wk4[locb] - work1;
                  wk1[locb] += work2;
                  aux5[locb] = -idxd;
                }
              }
            }
          while (true) {
            if (locd != loca) {
              loce = aux5[locd];
              aux5[locd] = 0;
              idxd = -aux5[loce];
              idxe = aux6[loce];
              aux4[locd] = -idxe;
              idxa = -aux4[loce];
              aux4[loce] = -idxd;
              locd = core[idxa];
            }
            else {
              if (loca == tmpoptx) break;
              aux5[loca] = 0;
              aux4[loca] = -idxc;
              aux4[tmpoptx] = -idxb;
              loca = tmpoptx;
              locd = hub;
            }
          }
          aux5[tmpoptx] = 0;
          idxa = 1;
          if (loch <= 2) {
            // generate the original graph by expanding all pseudonodes
            wt = 0;
            for (locb=1; locb<=n; locb++)
              if (core[locb] == locb) {
                idxb = -aux4[locb];
                if (idxb != nplus) {
                  if (idxb >= 0) {
                    loca = core[idxb];
                    idxc = -aux4[loca];
                    tmparg[0] = position;
                    cpt_DuplicateEdges(neighbor,next,idxb,idxc,tmparg);
                    position = tmparg[0];
                    work1 = -(float) (weight[position]);
                    work1 += wk1[locb] + wk1[loca];
                    work1 += wk2[idxb] + wk2[idxc];
                    if (Math.abs(work1) > eps) {
                      sol[0][0] = 3;
                      return;
                    }
                    wt += weight[position];
                    aux4[loca] = idxb;
                    aux4[locb] = idxc;
                  }
                }
              }
            for (locb=1; locb<=n; locb++) {
              while (true) {
                if (aux1[locb] == locb) break;
                hub = core[locb];
                loca = aux1[hub];
                idxb = aux5[loca];
                if (idxb > 0) {
                  idxd = aux2[loca];
                  locd = loca;
                  tmparg[0] = locd;
                  cpt_ExpandBlossom(core,aux1,aux3,wk1,wk2,tmparg,idxd);
                  locd = tmparg[0];
                  aux1[hub] = idxd;
                  work3 = wk3[loca];
                  wk1[hub] = work3;
                  while (true) {
                    wk2[idxd] -= work3;
                    if (idxd == hub) break;
                    idxd = aux1[idxd];
                  }
                  idxb = aux4[hub];
                  locd = core[idxb];
                  if (locd != hub) {
                    loca = aux5[locd];
                    loca = core[loca];
                    idxd = aux4[locd];
                    aux4[locd] = idxb;
                    do {
                      loce = core[idxd];
                      idxb = aux5[loce];
                      idxc = aux6[loce];
                      locd = core[idxb];
                      tmparg[0] = position;
                      cpt_DuplicateEdges(neighbor,next,idxb,idxc,tmparg);
                      position = tmparg[0];
                      work1 = -(float)(weight[position]);
                      wt += weight[position];
                      work1 += wk1[locd] + wk1[loce];
                      work1 += wk2[idxb] + wk2[idxc];
                      if (Math.abs(work1) > eps) {
                        sol[0][0] = 3;
                        return;
                      }
                      aux4[loce] = idxc;
                      idxd = aux4[locd];
                      aux4[locd] = idxb;
                    } while (locd != hub);
                    if (loca == hub) continue;
                  }
                  while (true) {
                    idxd = aux4[loca];
                    locd = core[idxd];
                    idxe = aux4[locd];
                    tmparg[0] = position;
                    cpt_DuplicateEdges(neighbor,next,idxd,idxe,tmparg);
                    position = tmparg[0];
                    wt += weight[position];
                    work1 = -(float)(weight[position]);
                    work1 += wk1[loca] + wk1[locd];
                    work1 += wk2[idxd] + wk2[idxe];
                    if (Math.abs(work1) > eps) {
                      sol[0][0] = 3;
                      return;
                    }
                    aux4[loca] = idxe;
                    aux4[locd] = idxd;
                    idxc = aux5[locd];
                    loca = core[idxc];
                    if (loca == hub) break;
                  }
                  break;
                }
                else {
                  idxc = aux4[hub];
                  aux1[hub] = hub;
                  work3 = wk2[hub];
                  wk1[hub] = 0f;
                  wk2[hub] = 0f;
                  do {
                    idxe = aux3[loca];
                    idxd = aux1[idxe];
                    tmparg[0] = loca;
                    cpt_ExpandBlossom(core,aux1,aux3,wk1,wk2,tmparg,idxd);
                    loca = tmparg[0];
                    loce = core[idxc];
                    if (loce != loca) {
                      idxb = aux4[loca];
                      tmparg[0] = position;
                      cpt_DuplicateEdges(neighbor,next,hub,idxb,tmparg);
                      position = tmparg[0];
                      work1 = -(float)(weight[position]);
                      wt += weight[position];
                      work1 += wk2[idxb] + wk1[loca] + work3;
                      if (Math.abs(work1) > eps) {
                        sol[0][0] = 3;
                        return;
                      }
                    }
                    else
                      aux4[loca] = idxc;
                    loca = idxd;
                  } while (loca != hub);
                }
              }
            }
            // store up the duplicate edges
            duparcs = 0;
            i = next[2];
            for (p=2; p<=n; p++) {
              loch = next[p+1] - 1;
              for (q=i; q<=loch; q++) {
                idxd = neighbor[q];
                if (idxd <= 0) {
                  idxd = -idxd;
                  if (idxd <= p) {
                    duparcs++;
                    sol[duparcs][1] = p;
                    sol[duparcs][2] = idxd;
                  }
                }
              }
              i = loch + 1;
            }
            cpt_Trail(n,neighbor,weight,next,aux3,core,startnode);
            // store up the optimal trail
            trail[1] = startnode;
            cur = startnode;
            curnext = 1;        
            do {
              p = next[cur];
              q = aux3[cur];
              complete = true;
              for (i=q; i>=p; i--) {
                if (weight[i] > 0) {
                  curnext++;
                  trail[curnext] = weight[i];
                  cur = weight[i];
                  weight[i] = -1;
                  complete = false;
                  break;
                }
              }
            } while (!complete);
            trail[0] = curnext;
            sol[3][0] = duparcs;
            sol[1][0] = totsolcost + wt;
            return;
          }
          tmparg[0] = idxa;
          cpt_SecondScan(neighbor,weight,next,high,core,aux1,aux2,
                         aux3,aux4,wk1,wk2,wk3,wk4,tmparg,n);
          idxa = tmparg[0];
          continue iterate;
        }
        loce = core[idxa];
        idxa = aux5[loce];
        locd = core[idxa];
      }
      while (true) {
        if (locd == hub) {
          // shrink a blossom
          work3 = wk1[hub] + work1 - wk3[hub];
          wk1[hub] = 0f;
          idxe = hub;
          do {
            wk2[idxe] += work3;
            idxe = aux1[idxe];
          } while (idxe != hub);
          idxd = aux1[hub];
          skip = false;
          if (hub != loca) skip = true;
          do {
            if (!skip) {
              loca = tmpoptx;
              loce = aux5[hub];
            }
            skip = false;
            while (true) {
              aux1[idxe] = loce;
              idxa = -aux4[loce];
              aux4[loce] = idxa;
              wk1[loce] += wk4[loce] - work1;
              idxe = loce;
              tmparg[0] = idxe;
              cpt_ShrinkBlossom(core,aux1,wk1,wk2,hub,tmparg);
              idxe = tmparg[0];
              aux3[loce] = idxe;
              locd = core[idxa];
              aux1[idxe] = locd;
              wk1[locd] += work1 - wk3[locd];
              idxe = locd;
              tmparg[0] = idxe;
              cpt_ShrinkBlossom(core,aux1,wk1,wk2,hub,tmparg);
              idxe = tmparg[0];
              aux3[locd] = idxe;
              if (loca == locd) break;
              loce = aux5[locd];
              aux5[locd] = aux6[loce];
              aux6[locd] = aux5[loce];
            }
            if (loca == tmpoptx) {
              aux5[tmpoptx] = idxb;
              aux6[tmpoptx] = idxc;
              break;
            }
            aux5[loca] = idxc;
            aux6[loca] = idxb;
          } while (hub != tmpoptx);
          aux1[idxe] = idxd;
          loca = aux1[hub];
          aux2[loca] = idxd;
          wk3[loca] = work3;
          aux5[hub] = 0;
          wk4[hub] = high;
          wk3[hub] = work1;
          cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,
                        aux3,aux4,wk1,wk2,wk3,wk4,hub);
          continue iterate;
        }
        locf = aux5[hub];
        aux5[hub] = 0;
        idxd = -aux4[locf];
        hub = core[idxd];
      }
    }
    else {
      if (aux5[tmpoptx] > 0) {
        loca = aux1[tmpoptx];
        if (loca != tmpoptx) {
          idxa = aux5[loca];
          if (idxa > 0) {
            // expand a blossom
            idxd = aux2[loca];
            locd = loca;
            tmparg[0] = locd;
            cpt_ExpandBlossom(core,aux1,aux3,wk1,wk2,tmparg,idxd);
            locd = tmparg[0];
            work3 = wk3[loca];
            wk1[tmpoptx] = work3;
            aux1[tmpoptx] = idxd;
            while (true) {
              wk2[idxd] -= work3;
              if (idxd == tmpoptx) break;
              idxd = aux1[idxd];
            }
            
            idxb = -aux4[tmpoptx];
            locd = core[idxb];
            idxc = aux4[locd];
            hub = core[idxc];
            if (hub != tmpoptx) {
              loce = hub;
              while (true) {
                idxa = aux5[loce];
                locd = core[idxa];
                if (locd == tmpoptx) break;
                idxa = aux4[locd];
                loce = core[idxa];
              }
              aux5[hub] = aux5[tmpoptx];
              aux5[tmpoptx] = aux6[loce];
              aux6[hub] = aux6[tmpoptx];
              aux6[tmpoptx] = idxa;
              idxd = aux4[hub];
              loca = core[idxd];
              idxe = aux4[loca];
              aux4[hub] = -idxb;
              locd = loca;
              while (true) {
                idxb = aux5[locd];
                idxc = aux6[locd];
                aux5[locd] = idxe;
                aux6[locd] = idxd;
                aux4[locd] = idxb;
                loce = core[idxb];
                idxd = aux4[loce];
                aux4[loce] = idxc;
                if (loce == tmpoptx) break;
                locd = core[idxd];
                idxe = aux4[locd];
                aux5[loce] = idxd;
                aux6[loce] = idxe;
              }
            }
            idxc = aux6[hub];
            locd = core[idxc];
            wk4[locd] = work1;
            if (locd != hub) {
              idxb = aux5[locd];
              loca = core[idxb];
              aux5[locd] = aux5[hub];
              aux6[locd] = idxc;
              do {
                idxa = aux4[locd];
                aux4[locd] = -idxa;
                loce = core[idxa];
                idxa = aux5[loce];
                aux5[loce] = -idxa;
                wk4[loce] = high;
                wk3[loce] = work1;
                locd = core[idxa];
                wk4[locd] = work1;
                cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,
                              aux3,aux4,wk1,wk2,wk3,wk4,loce);
              } while (locd != hub);
              aux5[hub] = aux6[loce];
              aux6[hub] = idxa;
              if (loca == hub) continue iterate;
            }
            loce = loca;
            do {
              idxa = aux4[loce];
              aux4[loce] = -idxa;
              locd = core[idxa];
              aux5[loce] = -locd;
              idxa = aux5[locd];
              aux4[locd] = -aux4[locd];
              loce = core[idxa];
              aux5[locd] = -loce;
            } while (loce != hub);
            do {
              locd = -aux5[loca];
              tmparg[0] = loca;
              cpt_SecondScan(neighbor,weight,next,high,core,aux1,aux2,
                             aux3,aux4,wk1,wk2,wk3,wk4,tmparg,loca);
              loca = tmparg[0];
              loca = -aux5[locd];
              tmparg[0] = locd;
              cpt_SecondScan(neighbor,weight,next,high,core,aux1,aux2,
                             aux3,aux4,wk1,wk2,wk3,wk4,tmparg,locd);
              locd = tmparg[0];
            } while (loca != hub);
            continue iterate;
          }
        }
        // modify a blossom
        wk4[tmpoptx] = high;
        wk3[tmpoptx] = work1;
        i = 1;
        wk1[tmpoptx] = 0f;
        idxa = -aux4[tmpoptx];
        loca = core[idxa];
        idxb = aux4[loca];
        if (idxb == tmpoptx) {
          i = 2;
          aux4[loca] = idxa;
          idxd = aux1[tmpoptx];
          aux1[tmpoptx] = loca;
          wk1[loca] += work1 - wk3[loca];
          idxe = loca;
          tmparg[0] = idxe;
          cpt_ShrinkBlossom(core,aux1,wk1,wk2,tmpoptx,tmparg);
          idxe = tmparg[0];
          aux3[loca] = idxe;
          aux1[idxe] = idxd;
          idxb = aux6[tmpoptx];
          if (idxb == tmpoptx) {
            idxa = aux5[tmpoptx];
            loca = core[idxa];
            aux4[tmpoptx] = aux4[loca];
            aux4[loca] = idxa;
            aux5[tmpoptx] = 0;
            idxd = aux1[tmpoptx];
            aux1[tmpoptx] = loca;
            wk1[loca] += work1 - wk3[loca];
            idxe = loca;
            tmparg[0] = idxe;
            cpt_ShrinkBlossom(core,aux1,wk1,wk2,tmpoptx,tmparg);
            idxe = tmparg[0];
            aux3[loca] = idxe;
            aux1[idxe] = idxd;
            cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,
                          aux3,aux4,wk1,wk2,wk3,wk4,tmpoptx);
            continue iterate;
          }
        }
        do {
          idxc = tmpoptx;
          locd = aux1[tmpoptx];
          while (true) {
            idxd = locd;
            idxe = aux3[locd];
            skip = false;
            while (true) {
              if (idxd == idxb) {
                skip = true;
                break;
              }
              if (idxd == idxe) break;
              idxd = aux1[idxd];
            }
            if (skip) break;
            locd = aux1[idxe];
            idxc = idxe;
          }
          idxd = aux1[idxe];
          aux1[idxc] = idxd;
          tmparg[0] = locd;
          cpt_ExpandBlossom(core,aux1,aux3,wk1,wk2,tmparg,idxd);
          locd = tmparg[0];
          wk4[locd] = work1;
          if (i == 2) {
            aux5[locd] = aux5[tmpoptx];
            aux6[locd] = idxb;
            aux5[tmpoptx] = 0;
            aux4[tmpoptx] = aux4[locd];
            aux4[locd] = -tmpoptx;
            cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,
                          aux3,aux4,wk1,wk2,wk3,wk4,tmpoptx);
            continue iterate;
          }
          i = 2;
          aux5[locd] = tmpoptx;
          aux6[locd] = aux4[locd];
          aux4[locd] = -idxa;
          idxb = aux6[tmpoptx];
          if (idxb == tmpoptx) {
            idxa = aux5[tmpoptx];
            loca = core[idxa];
            aux4[tmpoptx] = aux4[loca];
            aux4[loca] = idxa;
            aux5[tmpoptx] = 0;
            idxd = aux1[tmpoptx];
            aux1[tmpoptx] = loca;
            wk1[loca] += work1 - wk3[loca];
            idxe = loca;
            tmparg[0] = idxe;
            cpt_ShrinkBlossom(core,aux1,wk1,wk2,tmpoptx,tmparg);
            idxe = tmparg[0];
            aux3[loca] = idxe;
            aux1[idxe] = idxd;
            cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,
                          aux3,aux4,wk1,wk2,wk3,wk4,tmpoptx);
            continue iterate;
          }
        } while (core[idxb] == tmpoptx);
        aux5[locd] = aux5[tmpoptx];
        aux6[locd] = idxb;
        aux5[tmpoptx] = 0;
        locd = aux1[tmpoptx];
        if (locd == tmpoptx) {
          aux4[tmpoptx] = locg;
          tmpopty = tmpoptx;
          tmparg[0] = tmpopty;
          cpt_SecondScan(neighbor,weight,next,high,core,aux1,aux2,
                         aux3,aux4,wk1,wk2,wk3,wk4,tmparg,tmpoptx);
          tmpopty = tmparg[0];
          continue iterate;
        }
        idxe = aux3[locd];
        idxd = aux1[idxe];
        aux1[tmpoptx] = idxd;
        tmparg[0] = locd;
        cpt_ExpandBlossom(core,aux1,aux3,wk1,wk2,tmparg,idxd);
        locd = tmparg[0];
        aux4[tmpoptx] = -aux4[locd];
        aux4[locd] = -tmpoptx;
        locc = locd;
        tmparg[0] = locc;
        cpt_SecondScan(neighbor,weight,next,high,core,aux1,aux2,
                       aux3,aux4,wk1,wk2,wk3,wk4,tmparg,locd);
        locc = tmparg[0];
        tmpopty = tmpoptx;
        tmparg[0] = tmpopty;
        cpt_SecondScan(neighbor,weight,next,high,core,aux1,aux2,
                       aux3,aux4,wk1,wk2,wk3,wk4,tmparg,tmpoptx);
        tmpopty = tmparg[0];
        continue iterate;
      }
      else {
        // grow an alternating tree
        idxa = -aux4[tmpoptx];
        if (idxa <= n) {
          aux5[tmpoptx] = aux2[tmpoptx];
          aux6[tmpoptx] = aux3[tmpoptx];
          loca = core[idxa];
          aux4[loca] = -aux4[loca];
          wk4[loca] = high;
          wk3[loca] = work1;
          cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,aux3,
                        aux4,wk1,wk2,wk3,wk4,loca);
          continue iterate;
        }
        else {
          idxb = aux2[tmpoptx];
          loca = core[idxb];
          aux4[tmpoptx] = aux4[loca];
          wk4[tmpoptx] = high;
          wk3[tmpoptx] = work1;
          aux4[loca] = idxb;
          wk1[loca] += work1 - wk3[loca];
          idxe = loca;
          tmparg[0] = idxe;
          cpt_ShrinkBlossom(core,aux1,wk1,wk2,tmpoptx,tmparg);
          idxe = tmparg[0];
          aux3[loca] = idxe;
          aux1[tmpoptx] = loca;
          aux1[idxe] = tmpoptx;
          cpt_FirstScan(neighbor,weight,next,core,aux1,aux2,aux3,
                        aux4,wk1,wk2,wk3,wk4,tmpoptx);
          continue iterate;
        }
      }
    }
  }
}

public static void chromaticPolynomial(int n, int m, int nodei[],
           int nodej[], int cpoly1[], int cpoly2[], int cpoly3[])
{
  int i,j,k,mm,nn,maxmn,ncomp,index,nodeu,nodev,nodew,nodex,incr;
  int isub2,jsub2,ivertex,jvertex,loop,top,ilast,jlast;
  int isub1=0,jsub1=0,ix=0,iy=0,nodey=0;
  int istack[] = new int[((n*(m+m-n+1))/2)+1];
  int jstack[] = new int[((n*(m+m-n+1))/2)+1];
  boolean visit,nonpos,skip;

  top = 0;
  for (i=1; i<=n; i++)
    cpoly2[i] = 0;
  mm = m;
  nn = n;
  // find a spanning tree
  while (true) {
    maxmn = mm + 1;
    if (nn > mm) maxmn = nn + 1;
    for (i=1; i<=nn; i++)
      cpoly3[i] = -i;
    for (i=1; i<=mm; i++) {
      j = nodei[i];
      nodei[i] = cpoly3[j];
      cpoly3[j] = - maxmn - i;
      j = nodej[i];
      nodej[i] = cpoly3[j];
      cpoly3[j] = - maxmn - maxmn - i;
    }
    ncomp = 0;
    index = 0;
    nodew = 0;
    while (true) {
      nodew++;
      if (nodew > nn) break;
      nodev = cpoly3[nodew];
      if (nodev > 0) continue;
      ncomp++;
      cpoly3[nodew] = ncomp;
      if (nodev >= -nodew) continue;
      nodeu = nodew;
      nodex = -nodev;
      visit = true;
      isub2 = -nodev / maxmn;
      jsub2 = -nodev - isub2 * maxmn;
      while (true) {
        nodev = (isub2 == 1) ? nodei[jsub2] : nodej[jsub2];
        if (nodev > 0)
          if (nodev <= maxmn)
             if (cpoly3[nodev] >= 0) nodey = jsub2;
        if (nodev >= 0) {
          if (ix == 1) {
            nodex = Math.abs(nodei[iy]);
             nodei[iy] = nodeu;
          }
          else {
            nodex = Math.abs(nodej[iy]);
            nodej[iy] = nodeu;
          }
          ix = nodex / maxmn;
          iy = nodex - ix * maxmn;
          if (ix == 0) {
            skip = false;
            do { 
              if (nodey != 0) {
                index++;
                cpoly3[nodeu] = nodei[nodey] + nodej[nodey] - nodeu;
                nodei[nodey] = -index;
                nodej[nodey] = nodeu;
              }
              nodeu = iy;
              if (iy <= 0) {
                skip = true;
                break;
              }
              nodex = -cpoly3[nodeu];
              ix = nodex / maxmn;
              iy = nodex - ix * maxmn;
            } while (ix == 0);
            if (skip) break;
          }
          isub2 = 3 - ix;
          jsub2 = iy;
          continue;
        }
        if (nodev < -maxmn) {
          if (isub2 == 1)
            nodei[jsub2] = -nodev;
          else
            nodej[jsub2] = -nodev;
          isub2 = -nodev / maxmn;
          jsub2 = -nodev - isub2 * maxmn;
        }
        else {
          nodev = - nodev;
          if (isub2 == 1)
            nodei[jsub2] = 0;
          else
            nodej[jsub2] = 0;
          if (visit) {
            isub1 = isub2;
            jsub1 = jsub2;
            nodey = 0;
            visit = false;
          }
          else {
            if (isub1 == 1)
              nodei[jsub1] = nodev;
            else
              nodej[jsub1] = nodev;
            isub1 = isub2;
            jsub1 = jsub2;
            if (ix == 1) {
              nodex = Math.abs(nodei[iy]);
              nodei[iy] = nodeu;
            }
            else {
              nodex = Math.abs(nodej[iy]);
              nodej[iy] = nodeu;
            }
          }
          ix = nodex / maxmn;
          iy = nodex - ix * maxmn;
          if (ix == 0) {
            skip = false;
            do { 
              if (nodey != 0) {
                index++;
                cpoly3[nodeu] = nodei[nodey] + nodej[nodey] - nodeu;
                nodei[nodey] = -index;
                nodej[nodey] = nodeu;
              }
              nodeu = iy;
              if (iy <= 0) {
                skip = true;
                break;
              }
              nodex = -cpoly3[nodeu];
              ix = nodex / maxmn;
              iy = nodex - ix * maxmn;
            } while (ix == 0);
            if (skip) break;
          }
          isub2 = 3 - ix;
          jsub2 = iy;
        }
      }
    }
    for (i=1; i<=mm; i++) {
      while (true) {
        nodey = -nodei[i];
        if (nodey < 0) break;
        nodex = nodej[i];
        nodej[i] = nodej[nodey];
        nodej[nodey] = nodex;
        nodei[i] = nodei[nodey];
        nodei[nodey] = cpoly3[nodej[nodey]];
      }
    }
    for (i=1; i<=index; i++)
      cpoly3[nodej[i]] = cpoly3[nodei[i]];
    // if ncomp is not equal to 1, the graph is not connected
    if (ncomp != 1) break;
    if (mm < nn) {
      cpoly2[nn]++;
      if (top == 0) break;
      nn = istack[top];
      mm = jstack[top];
      top -= mm + 1;
      for (i=1; i<=mm; i++) {
        nodei[i] = istack[top + i];
        nodej[i] = jstack[top + i];
      }
      if (mm == nn)
        cpoly2[nn]++;
      else {
        top += mm;
        istack[top] = nn;
        jstack[top] = mm - 1;
      }
    }
    else {
      if (mm == nn)
        cpoly2[nn]++;
      else {
        for (i=1; i<=mm; i++) {
          top++;
          istack[top] = nodei[i];
          jstack[top] = nodej[i];
        }
        istack[top] = nn;
        jstack[top] = mm - 1;
      }
    }
    for (i=1; i<=n; i++)
      cpoly1[i] = 0;
    ivertex = (nodei[mm] < nodej[mm]) ? nodei[mm] : nodej[mm];
    jvertex = nodei[mm] + nodej[mm] - ivertex;
    loop = mm - 1;
    mm  = 0;
    for (i=1; i<=loop; i++) {
      ilast = nodei[i];
      if (ilast == jvertex) ilast = ivertex;
      if (ilast == nn) ilast = jvertex;
      jlast = nodej[i];
      if (jlast == jvertex) jlast = ivertex;
      if (jlast == nn) jlast = jvertex;
      if (ilast == ivertex) {
        if (cpoly1[jlast] != 0) continue;
        cpoly1[jlast] = 1;
      }
      if (jlast == ivertex) {
        if (cpoly1[ilast] != 0) continue;
        cpoly1[ilast] = 1;
      }
      mm++;
      nodei[mm] = ilast;
      nodej[mm] = jlast;
    }
    nn--;
  }
  for (i=1; i<=n; i++) {  
    cpoly1[i] = cpoly2[i];
    cpoly3[i] = cpoly2[i] * (1 - 2 * ((n-i) - ((n-i)/2) * 2));
  }
  for (i=1; i<=n; i++) {
    jvertex = 0;
    for (j=i; j<=n; j++) {
      jvertex = cpoly1[n + i - j] + jvertex;
      cpoly1[n + i - j] = jvertex;
    }
  }
  incr = 0;
  for (i=1; i<=n; i++) {
    jvertex = 0;
    for (j=i; j<=n; j++) {
      jvertex = cpoly3[n + i - j] + incr * jvertex;
      cpoly3[n + i - j] = jvertex;
    }
    incr++;
  }
}


public static boolean connected(int n, int m, int nodei[], int nodej[])
{
  int i,j,k,r,connect;
  int neighbor[] = new int[m + m + 1];
  int degree[] = new int[n + 1];
  int index[] = new int[n + 2];
  int aux1[] = new int[n + 1];
  int aux2[] = new int[n + 1];

  for (i=1; i<=n; i++)
    degree[i] = 0;
  for (j=1; j<=m; j++) {
    degree[nodei[j]]++;
    degree[nodej[j]]++;
  }
  index[1] = 1;
  for (i=1; i<=n; i++) {
    index[i+1] = index[i] + degree[i];
    degree[i] = 0;
  }
  for (j=1; j<=m; j++) {
    neighbor[index[nodei[j]] + degree[nodei[j]]] = nodej[j];
    degree[nodei[j]]++;
    neighbor[index[nodej[j]] + degree[nodej[j]]] = nodei[j];
    degree[nodej[j]]++;
  }
  for (i=2; i<=n; i++)
    aux1[i] = 1;
  aux1[1] = 0;
  connect = 1;
  aux2[1] = 1;
  k = 1;
  while (true) {
    i = aux2[k];
    k--;
    for (j=index[i]; j<=index[i+1]-1; j++) {
      r = neighbor[j];
      if (aux1[r] != 0) {
        connect++;
        if (connect == n) {
          connect /= n;
          if (connect == 1) return true;
          return false;
        }
        aux1[r] = 0;
        k++;
        aux2[k] = r;
      }
    }
    if (k == 0) {
      connect /= n;
      if (connect == 1) return true;
      return false;
    }
  }
}


public static void connectedComponents(int n, int m, int nodei[], int nodej[],
                                       int component[])
{
  int edges,i,j,numcomp,p,q,r,typea,typeb,typec,tracka,trackb;
  int compkey,key1,key2,key3,nodeu,nodev;
  int numnodes[] = new int[n+1];
  int aux[] = new int[n+1];
  int index[] = new int[3];
  
  typec=0;
  index[1] = 1;
  index[2] = 2;
  q = 2;
  for (i=1; i<=n; i++) {
    component[i] = -i;
    numnodes[i] = 1;
    aux[i] = 0;   
  }   
  j = 1;
  edges = m;
  do {
    nodeu = nodei[j];
    nodev = nodej[j];
    key1 = component[nodeu];
    if (key1 < 0) key1 = nodeu;
    key2 = component[nodev];
    if (key2 < 0) key2 = nodev;
    if (key1 == key2) {
      if(j >= edges) {
        edges--;
        break;
      }
      nodei[j] = nodei[edges];
      nodej[j] = nodej[edges];
      nodei[edges] = nodeu;
      nodej[edges] = nodev;
      edges--;
    }
    else {
      if (numnodes[key1] >= numnodes[key2]) {
        key3 = key1;
        key1 = key2;
        key2 = key3;
        typec = -component[key2];
      }
      else
        typec = Math.abs(component[key2]);
      aux[typec] = key1;
      component[key2] = component[key1];
      i = key1;
      do {
        component[i] = key2;
        i = aux[i];
      } while (i != 0);
      numnodes[key2] += numnodes[key1];
      numnodes[key1] = 0;
      j++;
      if (j > edges || j > n) break;
    }
  } while (true);
  numcomp = 0;
  for (i=1; i<=n; i++)
    if (numnodes[i] != 0) {
      numcomp++;
      numnodes[numcomp] = numnodes[i];
      aux[i] = numcomp;
    }
  for (i=1; i<=n; i++) {
    key3 = component[i];
    if (key3 < 0) key3 = i;
    component[i] = aux[key3];
  }      
  if (numcomp == 1) {
    component[0] = numcomp;
    return;
  }
  typeb = numnodes[1];
  numnodes[1] = 1;
  for (i=2; i<=numcomp; i++) {
    typea = numnodes[i];
    numnodes[i] = numnodes[i-1] + typeb - 1;
    typeb = typea;
  }
  for (i=1; i<=edges; i++) {
    typec = nodei[i];
    compkey = component[typec];
    aux[i] = numnodes[compkey];
    numnodes[compkey]++;
  }
  for (i=1; i<=q; i++) {
    typea = index[i];
    do {
      if (typea <= i) break;
      typeb = index[typea];
      index[typea] = -typeb;
      typea = typeb;
    } while (true);
    index[i] = -index[i];
  }
  if (aux[1] >= 0)
    for (j=1; j<=edges; j++) {
      tracka = aux[j];
      do {
        if (tracka <= j) break;
        trackb = aux[tracka];
        aux[tracka] = -trackb;
        tracka = trackb;
      } while (true);
      aux[j] = -aux[j];
    }
  for (i=1; i<=q; i++) {
    typea = -index[i];
    if(typea >= 0) {
      r = 0;
      do {
        typea = index[typea];
        r++;
      } while (typea > 0);
      typea = i;
      for (j=1; j<=edges; j++)
        if (aux[j] <= 0) {
          trackb = j;
          p = r;
          do {
            tracka = trackb;
            key1 = (typea == 1) ? nodei[tracka] : nodej[tracka];
            do {
              typea = Math.abs(index[typea]);
              key1 = (typea == 1) ? nodei[tracka] : nodej[tracka];
              tracka = Math.abs(aux[tracka]);
              key2 = (typea == 1) ? nodei[tracka] : nodej[tracka];
              if (typea == 1)
                nodei[tracka] = key1;
              else
                nodej[tracka] = key1;
              key1 = key2;
              if (tracka == trackb) {
                p--;
                if (typea == i) break; 
              }
            } while (true);
            trackb = Math.abs(aux[trackb]);
          } while (p != 0);
        }
    }
  }
  for (i=1; i<=q; i++)
    index[i] = Math.abs(index[i]);
  if (aux[1] > 0) {
    component[0] = numcomp;
    return;
  }
  for (j=1; j<=edges; j++)
    aux[j] = Math.abs(aux[j]);
  typea=1;
  for (i=1; i<=numcomp; i++) {
    typeb = numnodes[i];
    numnodes[i] = typeb - typea + 1;
    typea = typeb;
  }
  component[0] = numcomp;
}


public static int cutNodes(int n, int m, int nodei[], int nodej[],
                           int cutnode[])
{
  int i,j,k,nodeu,nodev,node1,node2,node3,node4,numblocks;
  int root,p,edges,index,len1,len2,low,up,components;
  int totalcutnodes,numcutnodes=0;
  int firstedges[] = new int[n+1];
  int label[] = new int[n+1];
  int nextnode[] = new int[n+1];
  int length[] = new int[n+1];
  int cutvertex[] = new int[n+1];
  int cutedge[] = new int[m+1];
  boolean mark[] = new boolean[n+1];
  boolean join,iterate;

  totalcutnodes = 0;
  for (i=1; i<=n; i++)
    nextnode[i] = 0;
  components = 0;
  for (root=1; root<=n; root++) {
    if (nextnode[root] == 0) {
      components++;
      // set up the forward star representation of the graph
      k = 0;
      for (i=1; i<=n-1; i++) {
        firstedges[i] = k + 1;
        for (j=1; j<=m; j++) {
          nodeu = nodei[j];
          nodev = nodej[j];
          if ((nodeu == i) && (nodeu < nodev)) {
            k++;
            cutedge[k] = nodev;
          }
          else {
            if ((nodev == i) && (nodev < nodeu)) {
              k++;
              cutedge[k] = nodeu;
            }
          }
        }
      }
      firstedges[n] = m + 1;
      for (i=1; i<=n; i++) {
        label[i] = 0;
        mark[i] = false;
      }
      length[root] = 0;
      nextnode[root] = -1;
      label[root] = -root;
      index = 1;
      cutvertex[1] = root;
      edges = 2;
      do {
        node3 = cutvertex[index];
        index--;
        nextnode[node3] = -nextnode[node3];
        len1 = 0;
        for (node2=1; node2<=n; node2++) {
          join = false;
          if (node2 != node3) {
            if (node2 < node3) {
              nodeu = node2;
              nodev = node3;
            }
            else {
              nodeu = node3;
              nodev = node2;
            }
            low = firstedges[nodeu];
            up = firstedges[nodeu + 1];
            if (up > low) {
              up--;
              for (k=low; k<=up; k++) 
                if (cutedge[k] == nodev) {
                  join = true;
                  break;
                }
            }
          }
          if (join) {
            node1 = nextnode[node2];
            if (node1 == 0) {
              nextnode[node2] = -node3;
              index++;
              cutvertex[index] = node2;
              length[node2] = length[node3] + 1;
              label[node2] = -node2;
            }
            else {
              if (node1 < 0) {
                // next block
                node4 = label[node2];
                if (node4 > 0) mark[node4] = true;
                label[node2] = node3;
                len2 = length[node3] - length[-node1];
                if (len2 > len1)  len1 = len2;
              }
            }
          }
        }
        if (len1 > 0) {
          j = node3;
          while (true) {
            len1--;
            if (len1 < 0) break;
            p = label[j];
            if (p > 0) mark[p] = true;
            label[j] = node3;
            j = nextnode[j];
          }
          for (i=1; i<=n; i++) {
            p = label[i];
            if (p > 0)
              if (mark[p]) label[i] = node3;
          }
        }
        edges++;
      } while ((edges <= n) && (index > 0));
      nextnode[root] = 0;
      node3 = cutvertex[1];
      nextnode[node3] = Math.abs(nextnode[node3]);
      numblocks = 0;
      numcutnodes = 0;
      for (i=1; i<=n; i++)
        if (i != root) {
          node3 = label[i];
          if (node3 < 0) {
            numblocks++;
            label[i] = n + numblocks;
          }
          else {
            if ((node3 <= n) && (node3 > 0)) {
              numblocks++;
              node4 = n + numblocks;
              for (j=i; j<=n; j++)
                if (label[j] == node3) label[j] = node4;
            }
          }
        }
      for (i=1; i<=n; i++) {
        p = label[i];
        if (p > 0) label[i] = p - n;
      }
      i = 1;
      while (nextnode[i] != root)
        i++;
      label[root] = label[i];
      for (i=1; i<=n; i++) {
        node1 = nextnode[i];
        if (node1 > 0) {
          p = Math.abs(label[node1]);
          if (Math.abs(label[i]) != p) label[node1] = -p;
        }
      }
      for (i=1; i<=n; i++)
        if (label[i] < 0) numcutnodes++;
      // store the cut nodes
      j = 0;
      for (i=1; i<=n; i++)
        if (label[i] < 0) {
          j++;
          cutvertex[j] = i;
        }
      //  find the end-nodes
      for (i=1; i<=n; i++)
        length[i] = 0;
      for (i=1; i<=m; i++) {
        j = nodei[i];
        length[j]++;
        j = nodej[i];
        length[j]++;
      }
      for (i=1; i<=n; i++)
        if (length[i] == 1)
          if (label[i] > 0) label[i] = -label[i];
      for (p=1; p<=numcutnodes; p++) {
        totalcutnodes++;
        cutnode[totalcutnodes] = cutvertex[p];
      }
    }
  }
  cutnode[0] = totalcutnodes;
  return components;
}


public static void depthFirstSearch(int n, int m, int nodei[],
                    int nodej[], int parent[], int sequence[])
{
  int i,j,k,counter,stackindex,p,q,u,v;
  int stack[] = new int[n+1];
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+1];
  boolean mark[] = new boolean[m+1];
  boolean skip,found;

  // set up the forward star representation of the graph
  for (j=1; j<=m; j++)
    mark[j] = true;
  firstedges[1] = 0;
  k = 0;
  for (i=1; i<=n; i++) {
    for (j=1; j<=m; j++)
      if (mark[j]) {
        if (nodei[j] == i) {
          k++;
          endnode[k] = nodej[j];
          mark[j] = false;
        }
        else {
          if (nodej[j] == i) {
            k++;
            endnode[k] = nodei[j];
            mark[j] = false;
          }
        }
      }
    firstedges[i+1] = k;
  }
  for (i=1; i<=n; i++) {
    sequence[i] = 0;
    parent[i] = 0;
    stack[i] = 0;
  } 
  counter = 0;
  p = 1;
  stackindex = 0;
  // process descendents of node p
  while (true) {
    skip = false;
    counter++;
    parent[p] = 0;
    sequence[p] = counter;
    stackindex++;
    stack[stackindex] = p;
    while (true) {
      skip = false;
      q = 0;
      while (true) {
        q++;
        if (q <= n) {
          // check if p and q are adjacent
          if (p < q) {
            u = p;
            v = q;
          }
          else {
            u = q;
            v = p;
          }
          found = false;
          for (k=firstedges[u]+1; k<=firstedges[u+1]; k++)
            if (endnode[k] == v) {
              // u and v are adjacent
              found = true;
              break;
            }
          if (found && sequence[q] == 0) {
            stackindex++;
            stack[stackindex] = q;
            parent[q] = p;
            counter++;
            sequence[q] = counter;
            p = q;
            if (counter == n) return;
            break;
          }
        }
        else {
          // back up
          stackindex--;
          if (stackindex > 0) {
            q = p;
            p = stack[stackindex];
          }
          else {
            skip = true;
            break;
          }
        }
      }
      if (skip) break;
    }
    // process the next component
    stackindex = 0;
    skip = false;
    for (k=1; k<=n; k++)
      if (sequence[k] == 0) {
        p = k;
        skip = true;
        break;
      }
    if (!skip) break;
  }
}


public static int edgeConnectivity(int n, int m, int nodei[], int nodej[])
{
  int i,j,k,m2,source,sink;
  int minimumcut[] = new int[n+1];
  int edgei[] = new int[4*m+1];
  int edgej[] = new int[4*m+1];
  int capac[] = new int[4*m+1];
  int arcflow[] = new int[4*m+1];
  int nodeflow[] = new int[4*m+1];

  k = n;
  source = 1;
  m2 = m + m;
  for (sink=2; sink<=n; sink++) {
    // construct the network
    for (i=1; i<=4*m; i++) {
      edgei[i] = 0;
      edgej[i] = 0;
      capac[i] = 0;
    }
    // duplicate the edges
    j = 0;
    for (i=1; i<=m; i++) {
      j++;
      edgei[j] = nodei[i];
      edgej[j] = nodej[i];
      capac[j] = 1;
      j++;
      edgei[j] = nodej[i];
      edgej[j] = nodei[i];
      capac[j] = 1;
    }
    // invoke the network flow algorithm
    maximumNetworkFlow(n,m2,edgei,edgej,capac,source,sink,
                       minimumcut,arcflow,nodeflow);
    if (nodeflow[source] < k) k = nodeflow[source];
  }
  return k;
}


public static void EulerCircuit(int n, int m, boolean directed,
                       int nodei[], int nodej[], int trail[])
{
  int i,j,k,p,index,len,traillength,stacklength;
  int endnode[] = new int[m+1];
  int stack[] = new int[m+m+1];
  boolean candidate[] = new boolean[m+1];

  // check for connectedness
  if (!connected(n,m,nodei,nodej)) {
    trail[0] = 1;
    return;
  }

  for (i=1; i<=n; i++) {
    trail[i] = 0;
    endnode[i] = 0;
  }
  if (directed) {
    // check if the directed graph is eulerian
    for (i=1; i<=m; i++) {
      j = nodei[i];
      trail[j]++;
      j = nodej[i];
      endnode[j]++;
    }
    for (i=1; i<=n; i++)
      if (trail[i] != endnode[i]) {
        trail[0] = 1;
        return;
      }
  }
  else {
    // check if the undirected graph is eulerian
    for (i=1; i<=m; i++) {
      j = nodei[i];
      endnode[j]++;
      j = nodej[i];
      endnode[j]++;
    }
    for (i=1; i<=n; i++)
      if ((endnode[i] - ((endnode[i] / 2) * 2)) != 0) {
        trail[0] = 1;
        return;
      }
  }
  // the input graph is eulerian
  trail[0] = 0;
  traillength = 1;
  stacklength = 0;
  // find the next edge
  while (true) {
    if (traillength == 1) {
         endnode[1] = nodej[1];
         stack[1] = 1;
         stack[2] = 1;
         stacklength = 2;
    }
    else {
      p = traillength - 1;
      if (traillength != 2)
        endnode[p] = nodei[trail[p]] + nodej[trail[p]] - endnode[p - 1];
      k = endnode[p];
      if (directed)
        for (i=1; i<=m; i++) 
          candidate[i] = k == nodei[i];
      else
        for (i=1; i<=m; i++)
          candidate[i] = (k == nodei[i]) || (k == nodej[i]);
      for (i=1; i<=p; i++) 
        candidate[trail[i]] = false;
      len = stacklength;
      for (i=1; i<=m; i++)
        if (candidate[i]) {
          len++;
          stack[len] = i;
        }
      stack[len + 1] = len - stacklength;
      stacklength = len + 1;
    }
    //  search further
    while (true) {
      index = stack[stacklength];
      stacklength--;
      if (index == 0) {
         traillength--;
         if (traillength != 0) continue;
         return;
      }
      else {
         trail[traillength] = stack[stacklength];
         stack[stacklength] = index - 1;
         if (traillength == m) return;
         traillength++;
         break;
      }
    }
  }
}


public static void fundamentalCycles(int n, int m, int nodei[], int nodej[],
                                     int fundcycle[][])
{
  int i,j,k,nodeu,nodev,components,numcycles,root,index,edges;
  int low,len,up,node1,node2,node3;
  int endnode[] = new int[m+1];
  int firstedges[] = new int[n+1];
  int nextnode[] = new int[n+1];
  int pointer[] = new int[n+1];
  int currentcycle[] = new int[n+1];
  boolean join;

  // set up the forward star representation of the graph
  k = 0;
  for (i=1; i<=n-1; i++) {
    firstedges[i] = k + 1;
    for (j=1; j<=m; j++) {
      nodeu = nodei[j];
      nodev = nodej[j];
      if ((nodeu == i) && (nodeu < nodev)) {
        k++;
        endnode[k] = nodev;
      }
      else {
        if ((nodev == i) && (nodev < nodeu)) {
          k ++;
          endnode[k] = nodeu;
        }
      }
    }
  }
  firstedges[n] = m + 1;
  for (i=1; i<=n; i++) 
    nextnode[i] = 0;
  components = 0;
  numcycles = 0;
  for (root=1; root<=n; root++) 
    if (nextnode[root] == 0) {
      components++;
      nextnode[root] = -1;
      index = 1;
      pointer[1] = root;
      edges = 2;
      do {
        node3 = pointer[index];
        index--;
        nextnode[node3] = -nextnode[node3];
        for (node2=1; node2<=n; node2++) {
          join = false;
          if (node2 != node3) {
            if (node2 < node3) {
              nodeu = node2;
              nodev = node3;
            }
            else {
              nodeu = node3;
              nodev = node2;
            }
            low = firstedges[nodeu];
            up = firstedges[nodeu + 1];
            if (up > low) {
              up--;
              for (k=low; k<=up; k++)
                if (endnode[k] == nodev) {
                  join = true;
                  break;
                }
            }
          }
          if (join) {
            node1 = nextnode[node2];
            if (node1 == 0) {
               nextnode[node2] = -node3;
               index++;
               pointer[index] = node2;
            }
            else {
              if (node1 < 0) {
                // generate the next cycle
                numcycles++;
                len = 3;
                node1 = -node1;
                currentcycle[1] = node1;
                currentcycle[2] = node2;
                currentcycle[3] = node3;
                i = node3;
                while (true) {
                  j = nextnode[i];
                  if (j == node1) break;
                  len++;
                  currentcycle[len] = j;
                  i = j;
                }
                // store the current fundamental cycle
                fundcycle[numcycles][0] = len;
                for (i=1; i<=len; i++)
                  fundcycle[numcycles][i] = currentcycle[i];
              }
            }
          }
        }
        edges++;
      } while ((edges <= n) && (index > 0));
      nextnode[root] = 0;
      node3 = pointer[1];
      nextnode[node3] = Math.abs(nextnode[node3]);
    }
  fundcycle[0][0] = numcycles;
  fundcycle[0][1] = components;
}


public static int girth(int n, int m, int nodei[], int nodej[])
{
  int diag,edges,i,j,k,nsquare,p,u,v;
  int adjlist[] = new int[n*n];
  int aux1[] = new int[n*n];
  int aux2[] = new int[n*n];
  boolean found;

  nsquare = n * n;
  // store the graph in adjlist
  for (i=0; i<nsquare; i++)
    adjlist[i] = 0;
  for (p=1; p<=m; p++) {
    i = nodei[p] - 1;
    j = nodej[p] - 1;
    adjlist[n * i + j] = 1;
  }
  // copy the adjacency list matrix
  for (i=0; i<nsquare; i++)
    aux1[i] = adjlist[i];
  // multiplication at most n times
  for (p=1; p<n; p++) {
    found = false;
    for (i=0; i<n; i++) {
      u = i * n;
      for (j=0; j<n; j++) {
        v = j * n;
        diag = u + j;
        aux2[diag] = 0;
        if (i != j) {
        for (k=0; k<n; k++)
          aux2[diag] += adjlist[u + k] * aux1[v + k];
          if (aux2[diag] > 1) {
            found = true;
            aux2[diag] = 1;
          }
        }
      }
    }
    for (i=0; i<nsquare; i++)
      if (aux1[i] + aux2[i] > 1) return 2 * p + 1;
    if (found) return 2 * (p + 1);
    // copy aux2 to aux1
    for (i=0; i<nsquare; i++)
      aux1[i] = aux2[i];
  }
  return 0;
}


public static int graphIsomorphism(int n, int adj1[][], int adj2[][],
                                   int map1[], int map2[])
{
  int i,j,k,edges1,edges2;
  int label1[][] = new int[n + 1][n + 1];
  int label2[][] = new int[n + 1][n + 1];
  int degree1[] = new int[n + 1];
  int degree2[] = new int[n + 1];

  // validate the number of edges
  edges1 = 0;
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      edges1 = (i == j) ? edges1 + 2 * adj1[i][j] : edges1 + adj1[i][j];
  edges1 /= 2;
  edges2 = 0;
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      edges2 = (i == j) ? edges2 + 2 * adj2[i][j] : edges2 + adj2[i][j];
  edges2 /= 2;
  if (edges1 != edges2 ) return 1;
  // validate the degree sequences
  // node degrees of the first graph are ordered in decreasing order
  for (i=1; i<=n; i++)  
    degree1[i] = 0;
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      degree1[i] += adj1[i][j];
  // sort "degree1" in descending order
  GraphTheoryAlgorithms.heapsort(n, degree1, false);
  // node degree of the second graph are ordered in decreasing order
  for (i=1; i<=n; i++)  
    degree2[i] = 0;
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      degree2[i] += adj2[i][j];
  // sort "degree2" in descending order
  GraphTheoryAlgorithms.heapsort(n, degree2, false);
  // compare the degree sequence of the two graphs
  k = 1;
  while (k <= n) {
    if (degree1[k] < degree2[k]) return 2;
    k++;
  }
  // compute the code of the first graph
  if (!isomorphicCode(adj1, n, label1, map1)) return 4; 
  // compute the code of the second graph
  if (!isomorphicCode(adj2, n, label2, map2)) return 4;
  // compare the codes of the two graphs
  for (j=2; j<=n; j++)
    for (i=1; i<=j-1; i++)
      if (label1[i][j] != label2[i][j]) return 3;
  return 0;
}


public static void HamiltonCycle(int n, int m, boolean directed,
                          int nodei[], int nodej[], int cycle[])
{
  int i,j,k,stacklen,lensol,stackindex,len,len1,len2,low,up;
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+m+1];
  int stack[] = new int[m+m+1];
  boolean connect[] = new boolean[n+1];
  boolean join,skip;

  // set up the forward star representation of the graph
  k = 0;
  for (i=1; i<=n; i++) {
    firstedges[i] = k + 1;
    for (j=1; j<=m; j++) {
      if (nodei[j] == i) {
        k++;
        endnode[k] = nodej[j];
      }
      if (!directed)
        if (nodej[j] == i) {
          k++;
          endnode[k] = nodei[j];
        }
    }
  }
  firstedges[n+1] = k + 1;
  // initialize
  lensol = 1;
  stacklen = 0;
  // find the next node
  while (true) {
    if (lensol == 1) {
      stack[1] = 1;
      stack[2] = 1;
      stacklen = 2;
    }
    else {
      len1 = lensol - 1;
      len2 = cycle[len1];
      for (i=1; i<=n; i++) {
        connect[i] = false;
        low = firstedges[len2];
        up = firstedges[len2 + 1];
        if (up > low) {
          up--;
          for (k=low; k<=up; k++)
            if (endnode[k] == i) {
              connect[i] = true;
              break;
            }
        }
      }
      for (i=1; i<=len1; i++) {
        len = cycle[i];
        connect[len] = false;
      }
      len = stacklen;
      skip = false;
      if (lensol != n) {
        for (i=1; i<=n; i++)
          if (connect[i]) {
            len++;
            stack[len] = i;
          }
        stack[len + 1] = len - stacklen;
        stacklen = len + 1;
      }
      else {
        for (i=1; i<=n; i++)
          if (connect[i]) {
            if (!directed) {
              if (i > cycle[2]) {
                stack[len + 1] = len - stacklen;
                stacklen = len + 1;
                skip = true;
                break;
              }
            }
            join = false;
            low = firstedges[i];
            up = firstedges[i + 1];
            if (up > low) {
              up--;
              for (k=low; k<=up; k++)
                if (endnode[k] == 1) {
                  join = true;
                  break;
                }
            }
            if (join) {
              stacklen += 2;
              stack[stacklen - 1] = i;
              stack[stacklen] = 1;
            }
            else {
              stack[len + 1] = len - stacklen;
              stacklen = len + 1;
            }
            skip = true;
            break;
          }
        if (!skip) {
          stack[len + 1] = len - stacklen;
          stacklen = len + 1;
        }
      }
    }
    // search further
    while (true) {
      stackindex = stack[stacklen];
      stacklen--;
      if (stackindex == 0) {
        lensol--;
        if (lensol == 0) {
          cycle[0] = 1;
          return;
        }
        continue;
      }
      else {
        cycle[lensol] = stack[stacklen];
        stack[stacklen] = stackindex - 1;
        if (lensol == n) {
          cycle[0] = 0;
          return;
        }
        lensol++;
        break;
      }
    }
  }
}


public static void heapsort(int n, int x[], boolean ascending)
{
  /* sort array elements x[1], x[2],..., x[n] in the order of
   *  increasing (ascending=true) or decreasing (ascending=false)
   */
    
  int elm,h,i,index,k,temp;

  if (n <= 1) return;
  // initially nodes n/2 to 1 are leaves in the heap
  for (i=n/2; i>=1; i--) {
    elm = x[i];
    index = i;
    while (true) {
      k = 2 * index;
      if (n < k) break;
      if (k + 1 <= n) {
        if (ascending) {
          if (x[k] < x[k+1]) k++;
        }
        else {
          if (x[k+1] < x[k]) k++;
        }
      }
      if (ascending) {
        if (x[k] <= elm) break;
      }
      else {
        if (elm <= x[k]) break;
      }
      x[index] = x[k];
      index = k;
    }
    x[index] = elm;
  }
  // swap x[1] with x[n]
  temp = x[1];
  x[1] = x[n];
  x[n] = temp;
  // repeat delete the root from the heap 
  for (h=n-1; h>=2; h--) {
    // restore the heap structure of x[1] through x[h]
    for (i=h/2; i>=1; i--) {
      elm = x[i];
      index = i;
      while (true) {
        k = 2 * index;
        if (h < k) break;
        if (k + 1 <= h) {
          if (ascending) {
            if (x[k] < x[k+1]) k++;
          }
          else {
            if (x[k+1] < x[k]) k++;
          }
        }
        if (ascending) {
          if (x[k] <= elm) break;
        }
        else {
          if (elm <= x[k]) break;
        }
        x[index] = x[k];
        index = k;
      }
      x[index] = elm;
    }
    // swap x[1] and x[h]
    temp = x[1];
    x[1] = x[h];
    x[h] = temp;
  }
}


public static void kShortestPaths(int n, int m, int nodei[], int nodej[],
                                  int weight[], int k, int source, int sink,
                                  int pathnodes[], int dist[][], int path[][])
{
  int i,j,init,iter,numforward,numbackward,large,nodeu,nodev;  
  int ids1,ids2,max,index,index1,index2,index3;
  int length1,length2,length3,loop1,loop2,len,sub1=0,sub2=0;
  int elm,elmnodei,elmweight,h,p,temp;
  int arcforward[] = new int[n+1];
  int arcbackward[] = new int[n+1];
  int aux[] = new int[k+1];
  boolean best,skip;

  // use heapsort to sort the nodej array in ascending order
  // initially nodes m/2 to 1 are leaves in the heap
  for (i=m/2; i>=1; i--) {
    elm = nodej[i];
    elmnodei = nodei[i];
    elmweight = weight[i];
    index = i;
    while (true) {
      p = 2 * index;
      if (m < p) break;
      if (p + 1 <= m)
        if (nodej[p] < nodej[p+1]) p++;
      if (nodej[p] <= elm) break;
      nodej[index] = nodej[p];
      nodei[index] = nodei[p];
      weight[index] = weight[p];
      index = p;
    }
    nodej[index] = elm;
    nodei[index] = elmnodei;
    weight[index] = elmweight;
  }
  // swap nodej[1] with nodej[m]
  temp = nodej[1];
  nodej[1] = nodej[m];
  nodej[m] = temp;
  temp = nodei[1];
  nodei[1] = nodei[m];
  nodei[m] = temp;
  temp = weight[1];
  weight[1] = weight[m];
  weight[m] = temp;
  // repeat delete the root from the heap 
  for (h=m-1; h>=2; h--) {
    // restore the heap structure of nodej[1] through nodej[h]
    for (i=h/2; i>=1; i--) {
      elm = nodej[i];
      elmnodei = nodei[i];
      elmweight = weight[i];
      index = i;
      while (true) {
        p = 2 * index;
        if (h < p) break;
        if (p + 1 <= h)
          if (nodej[p] < nodej[p+1]) p++;
        if (nodej[p] <= elm) break;
        nodej[index] = nodej[p];
        nodei[index] = nodei[p];
        weight[index] = weight[p];
        index = p;
      }
      nodej[index] = elm;
      nodei[index] = elmnodei;
      weight[index] = elmweight;
    }
    // swap nodej[1] and nodej[h]
    temp = nodej[1];
    nodej[1] = nodej[h];
    nodej[h] = temp;
    temp = nodei[1];
    nodei[1] = nodei[h];
    nodei[h] = temp;
    temp = weight[1];
    weight[1] = weight[h];
    weight[h] = temp;
  }
  // finish the heap sort
  init = 0;
  numforward = 0;
  numbackward = 0;
  for (i=1; i<=m; i++) {
    if (nodei[i] > nodej[i])
      numbackward++;
    else
      numforward++;
  }
  int adjbackward[] = new int[numbackward+1];
  int lenbackward[] = new int[numbackward+1];
  int adjforward[] = new int[numforward+1];
  int lenforward[] = new int[numforward+1];
  numforward = 0;
  numbackward = 0;
  index = 0;
  large = 1;
  for (i=1; i<=m; i++) {
    nodev = nodei[i];
    nodeu = nodej[i];
    len   = weight[i];
    large += (len > 0) ? len : 0;
    if (nodeu != index) {
      if (nodeu != index+1)
        for (j=index+1; j<=nodeu-1; j++) {
          arcbackward[j] = 0;
          arcforward[j] = 0;
        }
      if (init != 0) {
        arcforward[index] = sub1;
        arcbackward[index] = sub2;
      }
      sub1 = 0;
      sub2 = 0;
      index = nodeu;
    }
    init++;
    if (nodev <= nodeu) {
      numforward++;
      adjforward[numforward] = nodev;
      lenforward[numforward] = len;
      sub2++;
    }
    else {
      numbackward++;
      adjbackward[numbackward] = nodev;
      lenbackward[numbackward] = len;
      sub1++;
    }
  }
  arcbackward[index] = sub2;
  arcforward[index] = sub1;
  for (i=1; i<=n; i++) 
    for (j=1; j<=k; j++) 
      dist[i][j] = large;
  dist[source][1] = 0;
  iter = 1;
  while (true) {
    ids2 = numbackward;
    best = true;
    i = n - 1;
    while (i > 0) {
      if (arcforward[i] != 0) {
        ids1 = ids2 - arcforward[i] + 1;
        // matrix multiplication with dist using the lower
        // triangular part of the edge distance matrix
        for (j=1; j<=k; j++)
          aux[j] = dist[i][j];
        max = aux[k];
        for (loop1=ids1; loop1<=ids2; loop1++) {
          index1 = adjbackward[loop1];
          length3 = lenbackward[loop1];
          for (loop2=1; loop2<=k; loop2++) {
            length1 = dist[index1][loop2];
            if (length1 >= large) break;
            length2 = length1 + length3;
            if (length2 >= max) break;
            j = k;
            skip = false;
            while (true) {
              if (j >= 2) {
                if (length2 < aux[j-1])
                  j--;
                else {
                  if (length2 == aux[j-1]) skip = true;
                  break;
                }
              }
              else {
                j = 1;
                break;
              }
            }
            if (skip) continue;
            index2 = k;
            while (index2 > j) {
              aux[index2] = aux[index2 - 1];
              index2--;
            }
            aux[j] = length2;
            best = false;
            max = aux[k];
          }
        }
        if (!best)
          for (j=1; j<=k; j++)
            dist[i][j] = aux[j];
        ids2 = ids1 - 1;
      }
      i--;
    }
    if (iter != 1)
      if (best) break;
    iter++;
    ids1 = 1;
    best = true;
    for (i=2; i<=n; i++)
      if (arcbackward[i] != 0) {
        ids2 = ids1 + arcbackward[i] - 1;
        // matrix multiplication with dist using the upper
        // triangular part of the edge distance matrix
        for (j=1; j<=k; j++)
          aux[j] = dist[i][j];
        max = aux[k];
        for (loop1=ids1; loop1<=ids2; loop1++) {
          index1 = adjforward[loop1];
          length3 = lenforward[loop1];
          for (loop2=1; loop2<=k; loop2++) {
            length1 = dist[index1][loop2];
            if (length1 >= large) break;
            length2 = length1 + length3;
            if (length2 >= max) break;
            j = k;
            skip = false;
            while (true) {
              if (j >= 2) {
                if (length2 < aux[j-1])
                  j--;
                else {
                  if (length2 == aux[j-1]) skip = true;
                  break;
                }
              }
              else {
                j = 1;
                break;
              }
            }
            if (skip) continue;
            index2 = k;
            while (index2 > j) {
              aux[index2] = aux[index2 - 1];
              index2--;
            }
            aux[j] = length2;
            best = false;
            max = aux[k];
          }
        }
        if (!best)
          for (j=1; j<=k; j++)
            dist[i][j] = aux[j];
        ids1 = ids2 + 1;
      }
    if (!best) iter++;
  }
  // store at most maxpth number of k shortest paths
  // from source to sink, allowing repeated nodes
  int ptag,numpth,numpath,up,npmax,nt,nd;
  int maxpth,isub,jlen,lt;
  npmax = pathnodes[0];
  maxpth = pathnodes[1];
  int trailnode[] = new int[n+2];
  int neighbors[] = new int[m+1];
  int edgelength[] = new int[m+1];
  int currentnode[] = new int[npmax+1];
  int position[] = new int[npmax+1];
  int pathlength[] = new int[npmax+1];
  boolean nextp;

  init = 0;
  index = 0;
  large = 1;
  for (i=1; i<=m; i++) {
    large += weight[i];
    nodev = nodei[i];
    nodeu = nodej[i];
    len = weight[i];
    if (nodeu != index) {
      if (nodeu != index+1)
        for (j=index+1; j<=nodeu-1; j++)
          trailnode[j] = 0;
      trailnode[nodeu] = init + 1;
      index = nodeu;
    }
    init++;
    neighbors[init] = nodev;
    edgelength[init] = len;
  }
  trailnode[index + 1] = init + 1;
  for (i=1; i<=npmax; i++) {
    currentnode[i] = 0;
    position[i] = 0;
    pathlength[i] = 0;
  }
  numpath = 1;
  if (source == sink) numpath = 2;
  numpth = 0;
  if (dist[sink][numpath] >= large) {
    // no path exists from source to sink
    pathnodes[1] = 0;
    return;
  }
  do {
    ptag = 1;  
    length1 = dist[sink][numpath];
    if (length1 == large) return;
    length2 = length1;
    currentnode[1] = sink;
    do {
      nt = currentnode[ptag];
      ids1 = trailnode[nt];
      nd = nt;
      while ((trailnode[nd + 1] == 0) && (nd < n))
        nd++;
      up = trailnode[nd + 1] - 1;
      sub2 = ids1 + sub1;
      nextp = false;
      while (sub2 <= up) {
        isub = neighbors[sub2];
        jlen = edgelength[sub2];
        lt = length1 - jlen;
        j = 1;
        skip = false;
        while (true) {
          if ((dist[isub][j] > lt) || (j > k)) {
            sub2++;
            skip = true;
            break;
          }
          if (dist[isub][j] >= lt) break;
          j++;
        }
        if (skip) continue;
        ptag++;
        if (ptag > npmax) {
          // number of edges in a path exceeds allocated space
          pathnodes[0] = 0;
          return;
        }
        currentnode[ptag] = isub;
        position[ptag] = sub2 - ids1 + 1;
        pathlength[ptag] = jlen;
        length1 = lt;
        if (length1 != 0) {
          sub1 = 0;
          nextp = true;
          break;
        }
        if (isub != source) {
          sub1 = 0;
          nextp = true;
          break;
        }
        // store the current shortest path
        numpth++;
        for (j=1; j<=ptag; j++)
          path[numpth][j] = currentnode[ptag-j+1];
        pathnodes[1] = numpth;
        path[numpth][0] = ptag;
        path[numpth][npmax+1] = length2;
        if (numpth >= maxpth) return;
      }
      if (!nextp) {
        sub1 = position[ptag];
        currentnode[ptag] = 0;
        length1 += pathlength[ptag];
        ptag--;
      }
    } while (ptag > 0);
    numpath++;
  } while (numpath <= k);
}


public static void kShortestPathsNoRepeatedNodes(int n, int m,
                int nodei[], int nodej[], int weight[], int k,
                int source, int sink, int path[][])
{
  int i,j,jj,kk,large,tail,head,node1,node2,kp3,length;
  int incrs3,index1,index2,index3,index4,ncrs4,poolc;
  int j1,j2,j3,quefirstnode,linkst,treenode,examine,edge1,edge2;
  int bedge,cedge,dedge,jem1,jem2,njem1,njem2,njem3,numpaths;
  int upbound,lenrst,detb,deta,nrda,nrdb,quelast;
  int order,nrdsize,hda,hdb,count,queorder,quefirst,quenext;
  int pos1,pos2,pos3,auxda,auxdb,auxdc,low,high;
  int number,nump,sub1,sub2,sub3,jsub,nextnode;
  int nodep1=0,nodep2=0,nodep3=0,ncrs1=0,ncrs2=0,ncrs3=0;
  int edge3=0,lenga=0,lengb=0,mark1=0,mark2=0,mshade=0;
  int jump=0,incrs2=0,incrs4=0,jnd1=0,jnd2=0,jterm=0,jedge=0;
  int lentab=0,nqop1=0,nqop2=0,nqsize=0,nqin=0,parm=0;
  int nqout1=0,nqout2=0,nqp1=0,nqp2=0,poola=0,poolb=0;
  int incrs1=0,nqfirst=0,nqlast=0;
  int shortpathtree[] = new int[n+1];
  int treedist[] = new int[n+1];
  int arcnode[] = new int[n+1];
  int arcforward[] = new int[n+1];
  int arcbackward[] = new int[n+1];
  int auxstore[] = new int[n+1];
  int auxdist[] = new int[n+1];
  int auxtree[] = new int[n+1];
  int auxlink[] = new int[n+1];
  int nextforward[] = new int[m+1];
  int nextbackward[] = new int[m+1];
  int queuefirstpath[] = new int[k+4];
  int queuenextpath[] = new int[k+4];
  int queuesearch[] = new int[k+4];
  int kpathlength[] = new int[k+4];  
  int maxqueuelength = 10 * k;
  int crossarc[] = new int[maxqueuelength+1];
  int nextpoolentry[] = new int[maxqueuelength+1];
  boolean forwrd,lastno,noroom,goon,resultfound,getpaths;
  boolean loopon,lasta,lastb,rdfull,skip,force;
  boolean finem1=false,finem2=false,initsp=false,nostg=false;
  boolean invoke=false;

  kp3 = k + 3;
  // set up the network representation
  for (i=1; i<=n; i++) {
    arcforward[i] = 0;
    arcbackward[i] = 0;
  }
  large = 1;
  for (i=1; i<=m; i++) {
    large += weight[i];
    tail = nodei[i];
    head = nodej[i];
    nextforward[i] = arcforward[tail];
    arcforward[tail] = i;
    nextbackward[i] = arcbackward[head];
    arcbackward[head] = i;
  }
  // initialization
  for (i=1; i<=n; i++)
    auxdist[i] = large;
  for (i=1; i<=kp3; i++) 
    queuefirstpath[i] = 0;
  for (i=1; i<=maxqueuelength; i++)
    nextpoolentry[i] = i;
  nextpoolentry[maxqueuelength] = 0;
  // build the shortest distance tree
  // treedist[i] is used to store the shortest distance of node i from
  // source; shortpathtree[i] will contain the tree arc coming to node i,
  // it is negative when the direction of the arc is towards source,
  // it is zero if it is not reachable.
  for (i=1; i<=n; i++) {
    treedist[i] = large;
    shortpathtree[i] = 0;
    arcnode[i] = 0;
  }
  treedist[source] = 0;
  shortpathtree[source] = source;
  arcnode[source] = source;
  j = source;
  node1 = source;
  // examine neighbours of node j
  do {
    edge1 = arcforward[j];
    forwrd = true;
    lastno = false;
    do {
      if (edge1 == 0)
         lastno = true;
      else {
        length = treedist[j] + weight[edge1];
        if (forwrd) {
          node2 = nodej[edge1];
          edge2 = edge1;
        }
        else {
          node2 = nodei[edge1];
          edge2 = -edge1;
        }
        if (length < treedist[node2]) {
          treedist[node2] = length;
          shortpathtree[node2] = edge2;
          if (arcnode[node2] == 0) {
            arcnode[node1] = node2;
            arcnode[node2] = node2;
            node1 = node2;
          }
          else {
            if (arcnode[node2] < 0) {
              arcnode[node2] = arcnode[j];
              arcnode[j] = node2;
              if (node1 == j) {
                node1 = node2;
                arcnode[node2] = node2;
              }
            }
          }
        }
        if (forwrd)
          edge1 = nextforward[edge1];
        else
          edge1 = nextbackward[edge1];
      }
    } while (!lastno);
    jj = j;
    j = arcnode[j];
    arcnode[jj] = -1;
  } while (j != jj);
  // finish building the shortest distance tree
  numpaths = 0;
  resultfound = false;
  noroom = false;
  getpaths = false;
  if (shortpathtree[sink] == 0) {
    getpaths = true;
    resultfound = true;
  }
  if (!getpaths) {
    // initialize the storage pool
    i = 1;
    do {
      queuenextpath[i] = i;
      i++;
    } while (i <= k + 2);
    queuenextpath[k + 3] = 0;
    // initialize the priority queue
    lentab = kp3;
    low = -large;
    high = large;
    nqop1 = lentab;
    nqop2 = 0;
    nqsize = 0;
    nqin = 0;
    nqout1 = 0;
    nqout2 = 0;
    // obtain an entry from pool
    index1 = queuenextpath[1];
    queuenextpath[1] = queuenextpath[index1 + 1];
    index2 = queuenextpath[1];
    queuenextpath[1] = queuenextpath[index2 + 1];
    kpathlength[index1 + 1] = low;
    queuenextpath[index1 + 1] = index2;
    kpathlength[index2 + 1] = high;
    queuenextpath[index2 + 1] = 0;
    nqp1 = 0;
    nqp2 = 1;
    queuesearch[1] = index1;
    queuesearch[2] = index2;
    nqfirst = high;
    nqlast = low;
    // set the shortest path to the queue
    poola = queuenextpath[1];
    queuenextpath[1] = queuenextpath[poola + 1];
    poolb = poola;
    incrs1 = nextpoolentry[1];
    nextpoolentry[1] = nextpoolentry[incrs1 + 1];
    crossarc[incrs1 + 1] = shortpathtree[sink];
    nextpoolentry[incrs1 + 1] = 0;
    kpathlength[poola + 1] = treedist[sink];
    queuefirstpath[poola + 1] = incrs1;
    parm = poola;
    invoke = false;
  }
  // insert 'parm' into the priority queue
  iterate:
  while (true) {
    if (resultfound) break;
    order = kpathlength[parm + 1];
    pos1 = nqp1;
    pos2 = nqp2;
    while (pos2 - pos1 > 1) {
      pos3 = (pos1 + pos2) / 2;
      if (order > kpathlength[queuesearch[pos3 + 1] + 1])
        pos1 = pos3;
      else
        pos2 = pos3;
    }
    // linear search starting from queuesearch[pos1+1]
    index1 = queuesearch[pos1 + 1];
    do {
      index2 = index1;
      index1 = queuenextpath[index1 + 1];
    } while (kpathlength[index1 + 1] <= order);
    // insert between 'index1' and 'index2'
    queuenextpath[index2 + 1] = parm;
    queuenextpath[parm + 1] = index1;
    // update data in the queue
    nqsize = nqsize + 1;
    nqin = nqin + 1;
    nqop1 = nqop1 - 1;
    if (nqsize == 1) {
      nqfirst = order;
      nqlast = order;
    }
    else {
      if (order > nqlast)
        nqlast = order;
      else
        if (order < nqfirst) nqfirst = order;
    }
    if (nqop1 <= 0) {
      // reorganize
      index1 = queuesearch[nqp1 + 1];
      queuesearch[1] = index1;
      nqp1 = 0;
      index2 = queuesearch[nqp2 + 1];
      j3 = nqsize / lentab;
      j2 = j3 + 1;
      j1 = nqsize - ((nqsize / lentab) * lentab);
      if (j1 > 0)
        for (pos2=1; pos2<=j1; pos2++) {
          for (i=1; i<=j2; i++)
            index1 = queuenextpath[index1 + 1];
          queuesearch[pos2 + 1] = index1;
        }
      if (j3 > 0) {
        pos2 = j1 + 1;
        while (pos2 <= lentab - 1) {
          for (i=1; i<=j3; i++)
            index1 = queuenextpath[index1 + 1];
          queuesearch[pos2 + 1] = index1;
          pos2++;
        }
      }
      nqp2 = pos2;
      queuesearch[nqp2 + 1] = index2;
      nqop2 = nqop2 + 1;
      nqop1 = nqsize / 2;
      if (nqop1 < lentab) nqop1 = lentab;
    }
    force = false;
    if (invoke) {
      if (nostg) {
        resultfound = true;
        continue iterate;
      }
      force = true;
    }
    if (!force) {
      lenga = 0;
      mark1 = 0;
      initsp = true;
      for (i=1; i<=n; i++)
        arcnode[i] = 0;
    }
    // process the next path
    while (true) {
      if (!force) {
        mark1 = mark1 + 2;
        mark2 = mark1;
        mshade = mark1 + 1;
        // obtain the first entry from the priority queue
        if (nqsize > 0) {
          index2 = queuesearch[nqp1 + 1];
          index1 = queuenextpath[index2 + 1];
          queuenextpath[index2 + 1] = queuenextpath[index1 + 1];
          nqfirst = kpathlength[queuenextpath[index1 + 1] + 1];
          if (index1 == queuesearch[nqp1 + 2]) {
            nqp1++;
            queuesearch[nqp1 + 1] = index2;
          }
          nqop1--;
          nqsize--;
          nqout1++;
          poolc = index1;
        }
        else
          poolc = 0;
        if (poolc == 0) {
          // no more paths in queue, stop
          noroom = noroom && (numpaths < k);
          resultfound = true;
          continue iterate;
        }
        queuenextpath[poolb + 1] = poolc;
        poolb = poolc;
        numpaths++;
        if (numpaths > k) {
          noroom = false;
          numpaths--;
          resultfound = true;
          continue iterate;
        }
        lengb = kpathlength[poolc + 1];
        quefirstnode = queuefirstpath[poolc + 1];
        if (lengb < lenga) {
          resultfound = true;
          continue iterate;
        }
        lenga = lengb;
        // examine the tail of the arc
        incrs2 = quefirstnode;
        ncrs2 = source;
        nodep1 = n + 1;
        // obtain data of next path
        jump = 1;
      } 
      while (true) {
        if (!force) {
          // obtain data for the next path
          if (incrs2 == 0)
            linkst = 3;
          else {
            ncrs3 = ncrs2;
            incrs1 = incrs2;
            j = Math.abs(incrs1) + 1;
            incrs3 = crossarc[j];
            incrs2 = nextpoolentry[j];
            if (incrs3 > 0) {
              ncrs1 = nodei[incrs3];
              ncrs2 = nodej[incrs3];
              incrs4 = incrs3;
            }
            else {
              ncrs1 = nodej[-incrs3];
              ncrs2 = nodei[-incrs3];
              incrs4 = -incrs3;
            }
            finem1 = incrs2 <= 0;
            linkst = (ncrs2 == ncrs3) ? 2 : 1;
          }
          if (jump == 1) {
            lengb -= weight[incrs4];
            nodep1--;
            auxstore[nodep1] = incrs1;
            while (ncrs1 != ncrs3) {
              j = Math.abs(shortpathtree[ncrs1]);
              lengb -= weight[j];
              ncrs1 = (shortpathtree[ncrs1] > 0) ? nodei[j] : nodej[j];
            }
            if (!finem1) {
              jump = 1;
              continue;
            }
            // store the tail of the arc
            nodep2 = nodep1;
            finem2 = finem1;
            // obtain data of next path
            jump = 2;
            continue;
          }
          if (jump == 2) {
            if (linkst == 2) {
              nodep2--;
              auxstore[nodep2] = incrs4;
              weight[incrs4] += large;
              finem2 = finem1;
              // obtain data of next path
              jump = 2;
              continue;
            }
            // close the arc on the shortest path
            finem2 = finem2 && (linkst != 3);
            if (finem2) {
              edge3 = Math.abs(shortpathtree[ncrs3]);
              nodep2--;
              auxstore[nodep2] = edge3;
              weight[edge3] += large;
            }
          }
          if (jump == 3) {
            if (linkst == 1) {
              arcnode[ncrs2] = mark2;
              while (ncrs1 != ncrs3) {
                arcnode[ncrs1] = mark2;
                if (shortpathtree[ncrs1] > 0)
                  ncrs1 = nodei[shortpathtree[ncrs1]];
                else
                  ncrs1 = nodej[-shortpathtree[ncrs1]];
              }
              jump = 3;
              continue;
            }
            if (linkst == 2) {
              jump = 4;
              continue;
            }
          }
          if (jump == 4) {
            if (linkst == 2) {
              jump = 4;
              continue;
            }
          }
          // mark more nodes
          if (linkst != 3) {
            arcnode[ncrs2] = mark2;
            while (ncrs1 != ncrs3) {
              arcnode[ncrs1] = mark2;
              if (shortpathtree[ncrs1] > 0)
                ncrs1 = nodei[shortpathtree[ncrs1]];
              else
                ncrs1 = nodej[-shortpathtree[ncrs1]];
            }
            jump = 3;
            continue;
          }
          // generate descendants of the tail of the arc
          nodep3 = nodep1;
          incrs1 = auxstore[nodep3];
          jnd1 = crossarc[incrs1 + 1];
          // obtain the first node of the arc traversing forward
          jnd2 = (jnd1 < 0) ? nodei[-jnd1] : nodej[jnd1];
        }
        // process a section
        do {
          if (!force) {
            nodep3++;
            jterm = jnd2;
            jedge = jnd1;
            if (nodep3 > n)
              jnd2 = source;
            else {
              incrs2 = auxstore[nodep3];
              jnd1 = crossarc[incrs2 + 1];
              jnd2 = (-jnd1 > 0) ? nodei[-jnd1] : nodej[jnd1];
            }
          }
          // process a node
          do {
            if (!force) {
              mark1 += 2;
              treenode = mark1;
              examine = mark1 + 1;
              edge3 = Math.abs(jedge);
              weight[edge3] += large;
              if (initsp) initsp = (nqin < k);
              upbound = (initsp) ? large : nqlast;
              // obtain the restricted shortest path from source to jterm
              lenrst = upbound;
              bedge = 0;
              auxdist[jterm] = 0;
              auxtree[jterm] = 0;
              auxlink[jterm] = 0;
              jem1 = jterm;
              jem2 = jem1;
              // examine next node
              do {
                njem1 = jem1;
                auxda = auxdist[njem1];
                jem1 = auxlink[njem1];
                arcnode[njem1] = treenode;
                if (auxda + treedist[njem1] + lengb >= lenrst) continue;
                goon = true;
                lasta = false;
                edge1 = arcbackward[njem1];
                // loop through arcs from njem1
                do {
                  if (edge1 == 0)
                    lasta = true;
                  else {
                    // process the arc edge1
                    auxdb = auxda + weight[edge1];
                    if (goon) {
                      njem2 = nodei[edge1];
                      edge2 = edge1;
                      edge1 = nextbackward[edge1];
                    }
                    else {
                      njem2 = nodej[edge1];
                      edge2 = -edge1;
                      edge1 = nextforward[edge1];
                    }
                    if (arcnode[njem2] != mark2) {
                      auxdc = auxdb + lengb + treedist[njem2];
                      if (auxdc >= lenrst) continue;
                      if (arcnode[njem2] < mark2) {
                        if (shortpathtree[njem2] + edge2 == 0) {
                          arcnode[njem2] = mshade;
                        }
                        else {
                          // examine the status of the path
                          loopon = true;
                          njem3 = njem2;
                          while (loopon && (njem3 != source)) {
                            if (arcnode[njem3] < mark2) {
                              j = shortpathtree[njem3];
                              njem3 = (j > 0) ? nodei[j] : nodej[-j];
                            }
                            else
                              loopon = false;
                          }
                          if (loopon) {
                            // better path found
                            lenrst = auxdc;
                            bedge = edge2;
                            continue;
                          }
                          else {
                            njem3 = njem2;
                            lastb = false;
                            do {
                              if (arcnode[njem3] < mark2) {
                                arcnode[njem3] = mshade;
                                j = shortpathtree[njem3];
                                njem3 = (j > 0) ? nodei[j] : nodej[-j];
                              }
                              else
                                lastb = true;
                            } while (!lastb);
                          }
                        }
                      }
                      if ((arcnode[njem2] < treenode) ||
                                    (auxdb < auxdist[njem2])) {
                        // update node njem2
                        auxdist[njem2] = auxdb;
                        auxtree[njem2] = edge2;
                        if (arcnode[njem2] != examine) {
                          arcnode[njem2] = examine;
                          if (jem1 == 0) {
                            jem1 = njem2;
                            jem2 = njem2;
                            auxlink[njem2] = 0;
                          }
                          else {
                            if (arcnode[njem2] == treenode) {
                              auxlink[njem2] = jem1;
                              jem1 = njem2;
                            }
                            else {
                              auxlink[njem2] = 0;
                              auxlink[jem2] = njem2;
                              jem2 = njem2;
                            }
                          }
                        }
                      }
                    }
                  }
                } while (!lasta);
              } while (jem1 > 0);
              arcnode[jterm] = mark2;
              // finish processing the restricted path
              if ((bedge != 0) && (lenrst < upbound)) {
                detb = 0;
                cedge = bedge;
                do {
                  dedge = (cedge > 0) ? nodej[cedge] : nodei[-cedge];
                  if ((cedge != shortpathtree[dedge]) || (dedge == jterm)) {
                    detb++;
                    auxstore[detb] = cedge;
                  }
                  cedge = auxtree[dedge];
                } while (cedge != 0);
                // restore the path data
                deta = detb;
                nrda = nextpoolentry[1];
                quelast = large;
                nostg = false;
                while ((deta > 0) && (nrda > 0)) {
                  deta--;
                  nrda = nextpoolentry[nrda + 1];
                }
                rdfull = (!initsp) && (numpaths + nqsize >= k);
                skip = false;
                while (rdfull || (deta > 0)) {
                  // remove the last path from the queue
                  quelast = nqlast;
                  noroom = true;
                  rdfull = false;
                  // get the last entry from the priority queue
                  if (nqsize > 0) {
                    index4 = queuesearch[nqp2 + 1];
                    index3 = queuesearch[nqp2];
                    if (queuenextpath[index3 + 1] == index4) {
                      nqp2--;
                      queuesearch[nqp2 + 1] = index4;
                      index3 = queuesearch[nqp2];
                    }
                    index2 = index3;
                    while (index3 != index4) {
                      index1 = index2;
                      index2 = index3;
                      index3 = queuenextpath[index3 + 1];
                    }
                    queuenextpath[index1 + 1] = index4;
                    nqlast = kpathlength[index1 + 1];
                    nqop1--;
                    nrdsize = index2;
                    nqsize--;
                    nqout2++;
                  }
                  else
                    nrdsize = 0;
                  if (nrdsize == 0) {
                    nostg = true;
                    if (nostg) {
                      resultfound = true;
                      continue iterate;
                    }
                    skip = true;
                    break;
                  }
                  nrda = queuefirstpath[nrdsize + 1];
                  while (nrda > 0) {
                    j = nrda + 1;
                    deta--;
                    nrdb = nrda;
                    nrda = nextpoolentry[j];
                    nextpoolentry[j] = nextpoolentry[1];
                    nextpoolentry[1] = nrdb;
                  }
                  // put the entry nrdsize to pool
                  queuenextpath[nrdsize + 1] = queuenextpath[1];
                  queuenextpath[1] = nrdsize;
                }
                if (!skip) {
                  // build the entries of crossarc and nextpoolentry
                  if (lenrst >= quelast) {
                    if (nostg) {
                      resultfound = true;
                      continue iterate;
                    }
                  }
                  else {
                    nrdb = -incrs1;
                    deta = detb;
                    while (deta > 0) {
                      nrda = nextpoolentry[1];
                      nextpoolentry[1] = nextpoolentry[nrda + 1];
                      crossarc[nrda + 1] = auxstore[deta];
                      nextpoolentry[nrda + 1] = nrdb;
                      nrdb = nrda;
                      deta--;
                    }
                    // obtain the entry nrdsize from pool
                    nrdsize = queuenextpath[1];
                    queuenextpath[1] = queuenextpath[nrdsize + 1];
                    kpathlength[nrdsize + 1] = lenrst;
                    queuefirstpath[nrdsize + 1] = nrdb;
                    parm = nrdsize;
                    invoke = true;
                    continue iterate;
                  }
                }
              }
            }
            force = false;
            weight[edge3] -= large;
            lengb += weight[edge3];
            if (jterm != jnd2) {
              jterm = (jedge > 0) ? nodei[jedge] : nodej[-jedge];
              jedge = shortpathtree[jterm];
            }
          } while (jterm != jnd2);
          incrs1 = incrs2;
        } while (nodep3 <= n);
        // restore the join arcs
        while (nodep2 <= nodep1 - 1) {
          j = auxstore[nodep2];
          weight[j] -= large;
          nodep2++;
        }
        // repeat with the next path
        break;
      }
    }
  }
  if (!getpaths) {
    // sort the paths
    hdb = poola;
    count = 0;
    do {
      hda = hdb;
      count++;
      hdb = queuenextpath[hda + 1];
      queuenextpath[hda + 1] = count;
    } while (hda != poolb);
    // release all queue entries to the pool
    j = queuesearch[nqp2 + 1];
    queuenextpath[j + 1] = queuenextpath[1];
    queuenextpath[1] = queuesearch[nqp1 + 1];
    nqp1 = 0;
    nqp2 = 0;
    hdb = 0;
    do {
      j = hdb + 1;
      hdb = queuenextpath[j];
      queuenextpath[j] = 0;
    } while (hdb != 0);
    // exchanging records
    jj = k + 2;
    for (i=1; i<=jj; i++) {
      while ((queuenextpath[i + 1] > 0) && (queuenextpath[i + 1] != i)) {
        queorder = kpathlength[i + 1];
        quefirst = queuefirstpath[i + 1];
        quenext = queuenextpath[i + 1];
        j = queuenextpath[i + 1] + 1;
        kpathlength[i + 1] = kpathlength[j];
        queuefirstpath[i + 1] = queuefirstpath[j];
        queuenextpath[i + 1] = queuenextpath[j];
        kpathlength[quenext + 1] = queorder;
        queuefirstpath[quenext + 1] = quefirst;
        queuenextpath[quenext + 1] = quenext;
      }
    }
    kpathlength[1] = source;
    queuefirstpath[1] = sink;
    queuenextpath[1] = numpaths;
  }
  // construct the edges of the k shortest paths
  for (kk=1; kk<=numpaths; kk++) {
    number = 0;
    if ((kk <= 0) || (kk > queuenextpath[1])) {
      path[kk][0] = number;
      path[0][0] = numpaths;
      return;
    }
    index2 = kpathlength[1];
    length = kpathlength[kk + 1];
    sub3 = queuefirstpath[kk + 1];
    while (sub3 != 0) {
      jsub = Math.abs(sub3) + 1;
      index3 = index2;
      if (crossarc[jsub] > 0) {
        index1 = nodei[crossarc[jsub]];
        index2 = nodej[crossarc[jsub]];
      }
      else {
        index1 = nodej[-crossarc[jsub]];
        index2 = nodei[-crossarc[jsub]];
      }
      if (index2 != index3) {
        // store the arcs
        sub2 = n;
        arcnode[sub2] = crossarc[jsub];
        while (index1 != index3) {
          sub1 = shortpathtree[index1];
          sub2--;
          if (sub2 > 0)
            arcnode[sub2] = sub1;
          else
            nump = sub1;
          index1 = (sub1 > 0) ? nodei[sub1] : nodej[-sub1];
        }
        while (sub2 <= n) {
          number++;
          arcnode[number] = arcnode[sub2];
          sub2++;
        }
      }
      sub3 = nextpoolentry[jsub];
    }
    // 'number' is the number of edges in the path
    // 'length' is the length of the path
    // 'arcnode' is the array of edge numbers of the shortest path
    nextnode = source;
    count = 0;
    for (j=1; j<=number; j++) {
      i = arcnode[j];
      count++;
      if (nodei[i] == nextnode) {
        path[kk][count] = nextnode;
        nextnode = nodej[i];
      }
      else {
        path[kk][count] = nodej[i];
        nextnode = nodei[i];        
      }
    }
    count++;
    path[kk][count] = nextnode;
    path[kk][n+1] = length;
    path[kk][0] = count;
  }
  path[0][0] = numpaths;
}


public static void maximumConnectivity(int n, int k, int nodei[], int nodej[])
{
  int edges,halfk,halfn,i,j,nminus1,p,q,r;
  boolean evenk,evenn,join;

  // make an n-gon
  edges = 0;
  nminus1  = n - 1;
  halfk = k / 2;
  halfn = n / 2;
  for (i=1; i<=nminus1; i++) {
    edges++;
    nodei[edges] = i;
    nodej[edges] = i + 1;
  }
  edges++;
  nodei[edges] = n;
  nodej[edges] = 1;
  if (k == 2) return;
  evenk = (k == 2 * halfk) ? true : false;
  for (i=1; i<=nminus1; i++) {
    p = i + 1;
    for (j=p; j<=n; j++) {
      join = false;
      q = j - i;
      for (r=2; r<=halfk; r++)
        if (((r - ((r/n)*n)) == q) || (q + r == n)) join = true;
      if (join) {
        edges++;
        nodei[edges] = i;
        nodej[edges] = j;
      }
    }
  }
  // if k is even then finish
  if (evenk) return;
  evenn = (n == 2 * halfn) ? true : false;
  if (evenn) {
    // k is odd, n is even
    for (i=1; i<=halfn; i++) {
      edges++;
      nodei[edges] = i;
      nodej[edges] = i + halfn;
    }
  }
  else {
    // k is odd, n is odd
    p = (n + 1) / 2;
    q = (n - 1) / 2;
    for (i=2; i<=q; i++) {
      edges++;
      nodei[edges] = i;
      nodej[edges] = i + p;
    }
    edges++;
    nodei[edges] = 1;
    nodej[edges] = q + 1;
    edges++;
    nodei[edges] = 1;
    nodej[edges] = p + 1;
  }
}


public static void maximumNetworkFlow(int n, int m, int nodei[], 
              int nodej[], int capacity[], int source, int sink, 
              int minimumcut[], int arcflow[], int nodeflow[])
{
  int i,j,curflow,flag,medge,nodew,out;
  int in=0,iout=0,parm=0,m1=0,icont=0,jcont=0;
  int last=0,nodep=0,nodeq=0,nodeu=0,nodev=0,nodex=0,nodey=0;
  int firstarc[] = new int[n+1];
  int imap[] = new int[n+1];
  int jmap[] = new int[n+1];
  boolean finish,controla,controlb,controlc,controlg;
  boolean controld=false,controle=false,controlf=false;

  // create the artificial edges
  j = m;
  for (i=1; i<=m; i++) {
    j++;
    nodei[m+i] = nodej[i];
    nodej[m+i] = nodei[i];
    capacity[m+i] = 0;
  }
  m = m + m;
  // initialize
  for (i=1; i<=n; i++)
    firstarc[i] = 0;
  curflow = 0;
  for (i=1; i<=m; i++) {
    arcflow[i] = 0;
    j = nodei[i];
    if (j == source) curflow += capacity[i];
    firstarc[j]++;
  }
  nodeflow[source] = curflow;
  nodew = 1;
  for (i=1; i<=n; i++) {
    j = firstarc[i];
    firstarc[i] = nodew;
    imap[i] = nodew;
    nodew += j;
  }
  finish = false;
  controla = true;
  // sort the edges in lexicographical order
  entry1:
  while (true) {
    flag = 0;
    controlb = false;
    entry2:
    while (true) {
      if (!controlb) {
        if ((flag < 0) && controla) {
          if (flag != -1) {
            if (nodew < 0) nodep++;
            nodeq = jcont;
            jcont = nodep;
            flag = -1;
          }
          else {
            if (nodew <= 0) {
              if (icont > 1) {
                icont--;
                jcont = icont;
                controla = false;
                continue entry2;
              }
              if (m1 == 1)
                flag = 0;
              else {
                nodep = m1;
                m1--;
                nodeq = 1;
                flag = 1;
              }
            }
            else
              flag = 2;
          }
        }
        else {
          if (controla)
            if (flag > 0) {
              if (flag <= 1) jcont = icont;
              controla = false;
            }
          if (controla) {
            m1 = m;
            icont = 1 + m / 2;
            icont--;
            jcont = icont;
          }
          controla = true;
          nodep = jcont + jcont;
          if (nodep < m1) {
            nodeq = nodep + 1;
            flag = -2;
          }
          else {
            if (nodep == m1) {
              nodeq = jcont;
              jcont = nodep;
              flag = -1;
            }
            else {
              if (icont > 1) {
                icont--;
                jcont = icont;
                controla = false;
                continue entry2;
              }
              if (m1 == 1)
                flag = 0;
              else {
                nodep = m1;
                m1--;
                nodeq = 1;
                flag = 1;
              }
            }
          }
        }
      }
      controlg = false;
      controlc = false;
      if ((flag < 0) && !controlb) {
         nodew = nodei[nodep] - nodei[nodeq];
         if (nodew == 0) nodew = nodej[nodep] - nodej[nodeq];
         continue entry2;
      }
      else {
        if ((flag > 0) || controlb) {
          // interchange two edges
          controlb = false;
          nodew = nodei[nodep];
          nodei[nodep] = nodei[nodeq];
          nodei[nodeq] = nodew;
          curflow = capacity[nodep];
          capacity[nodep] = capacity[nodeq];
          capacity[nodeq] = curflow;
          nodew = nodej[nodep];
          nodej[nodep] = nodej[nodeq];
          nodej[nodeq] = nodew;
          curflow = arcflow[nodep];
          arcflow[nodep] = arcflow[nodeq];
          arcflow[nodeq] = curflow;
          if (flag > 0) continue entry2;
          if (flag == 0) {
            controlc = true;
          }
          else {
            jmap[nodev] = nodeq;
            controlg = true;
          }
        }
        else
          if (finish) {
            // return the maximum flow on each edge
            j = 0;
            for (i=1; i<=m; i++)
              if (arcflow[i] > 0) {
                j++;
                nodei[j] = nodei[i];
                nodej[j] = nodej[i];
                arcflow[j] = arcflow[i];  
              }
            arcflow[0] = j;
            return;
          }
      }
      if (!controlg && !controlc) {
        // set the cross references between edges
        for (i=1; i<=m; i++) {
          nodev = nodej[i];
          nodei[i] = imap[nodev];
          imap[nodev]++;
        }
      }
      entry3:
      while (true) {
        if (!controlg) {
          if (!controlc) {
            flag = 0;
            for (i=1; i<=n; i++) {
              if (i != source) nodeflow[i] = 0;
              jmap[i] = m + 1;
              if (i < n) jmap[i] = firstarc[i + 1];
              minimumcut[i] = 0;
            }
            in = 0;
            iout = 1;
            imap[1] = source;
            minimumcut[source] = -1;
            while (true) {
              in++;
              if (in > iout) break;
              nodeu = imap[in];
              medge = jmap[nodeu] - 1;
              last = firstarc[nodeu] - 1;
              while (true) {
                last++;
                if (last > medge) break;
                nodev = nodej[last];
                curflow = capacity[last] - arcflow[last];
                if ((minimumcut[nodev] != 0) || (curflow == 0)) continue;
                if (nodev != sink) {
                  iout++;
                  imap[iout] = nodev;
                }
                minimumcut[nodev] = -1;
              }
            }
            if (minimumcut[sink] == 0) {
              // exit
              for (i=1; i<=n; i++)
                minimumcut[i] = -minimumcut[i];
              for (i=1; i<=m; i++) {
                nodeu = nodej[nodei[i]];
                if (arcflow[i] < 0) nodeflow[nodeu] -= arcflow[i];
                nodei[i] = nodeu;
              }
              nodeflow[source] = nodeflow[sink];
              finish = true;
              continue entry1;
            }
            minimumcut[sink] = 1;
          }
          while (true) {
            if (!controlc) {
              in--;
              if (in == 0) break;
              nodeu = imap[in];
              nodep = firstarc[nodeu] - 1;
              nodeq = jmap[nodeu] - 1;
            }
            controlc = false;
            while (nodep != nodeq) {
              nodev = nodej[nodeq];
              if ((minimumcut[nodev] <= 0) || 
                  (capacity[nodeq] == arcflow[nodeq])) {
                nodeq--;
                continue;
              }
              else {
                nodej[nodeq] = -nodev;
                capacity[nodeq] -= arcflow[nodeq];
                arcflow[nodeq] = 0;
                nodep++;
                if (nodep < nodeq) {
                  nodei[nodei[nodep]] = nodeq;
                  nodei[nodei[nodeq]] = nodep;
                  controlb = true;
                  continue entry2;
                }
                break;
              }
            }
            if (nodep >= firstarc[nodeu]) minimumcut[nodeu] = nodep;
          }
          nodex = 0;
          for (i=1; i<=iout; i++)
            if (minimumcut[imap[i]] > 0) {
              nodex++;
              imap[nodex] = imap[i];
            }
          // find a feasible flow
          flag = -1;
          nodey = 1;
        }
        entry4:
        while (true) {
          if (!controlg) {
            if (!controlf) {
              if (!controld && !controle)
                nodeu = imap[nodey];
              if ((nodeflow[nodeu] <= 0) || controld || controle) {
                if (!controle) {
                  controld = false;
                  nodey++;
                  if (nodey <= nodex) continue entry4;
                  parm = 0;
                }
                controle = false;
                nodey--;
                if (nodey != 1) {
                  nodeu = imap[nodey];
                  if (nodeflow[nodeu] < 0) {
                    controle = true;
                    continue entry4;
                  }
                  if (nodeflow[nodeu] == 0) {
                    // accumulating flows
                    medge = m + 1;
                    if (nodeu < n) medge = firstarc[nodeu + 1];
                    last = jmap[nodeu];
                    jmap[nodeu] = medge;
                    while (true) {
                      if (last == medge) {
                        controle = true;
                        continue entry4;
                      }
                      j = nodei[last];
                      curflow = arcflow[j];
                      arcflow[j] = 0;
                      capacity[j] -= curflow;
                      arcflow[last] -= curflow;
                      last++;
                    }
                  }
                  if (firstarc[nodeu] > minimumcut[nodeu]) {
                    last = jmap[nodeu];
                    do {
                      j = nodei[last];
                      curflow = arcflow[j];
                      if (nodeflow[nodeu] < curflow) curflow = nodeflow[nodeu];
                      arcflow[j] -= curflow;
                      nodeflow[nodeu] -= curflow;
                      nodev = nodej[last];
                      nodeflow[nodev] += curflow;
                      last++;
                    } while (nodeflow[nodeu] > 0);
                    nodeflow[nodeu] = -1;
                    controle = true;
                    continue entry4;
                  }
                  last = minimumcut[nodeu] + 1;
                  controlf = true;
                  continue entry4;
                }
                for (i=1; i<=m; i++) {
                  nodev = -nodej[i];
                  if (nodev >= 0) {
                    nodej[i] = nodev;
                    j = nodei[i];
                    capacity[i] -= arcflow[j];
                    curflow = arcflow[i] - arcflow[j];
                    arcflow[i] = curflow;
                    arcflow[j] = -curflow;
                  }
                }
                continue entry3;
              }
              // an outgoing edge from a node is given maximum flow
              last = minimumcut[nodeu] + 1;
            }
          }
          while (true) {
            if (!controlg) {
              controlf = false;
              last--;
              if (last < firstarc[nodeu]) break;
              nodev = -nodej[last];
              if (nodeflow[nodev] < 0) continue;
              curflow = capacity[last] - arcflow[last];
              if (nodeflow[nodeu] < curflow) curflow = nodeflow[nodeu];
              arcflow[last] += curflow;
              nodeflow[nodeu] -= curflow;
              nodeflow[nodev] += curflow;
              parm = 1;
              nodep = nodei[last];
              nodeq = jmap[nodev] - 1;
              if (nodep < nodeq) {
                nodei[nodei[nodep]] = nodeq;
                nodei[nodei[nodeq]] = nodep;
                controlb = true;
                continue entry2;
              }
              if (nodep == nodeq) jmap[nodev] = nodeq;
            }
            controlg = false;
            if (nodeflow[nodeu] > 0) continue;
            if (capacity[last] == arcflow[last]) last--;
            break;
          }
          minimumcut[nodeu] = last;
          if (parm != 0) {
            controld = true;
            continue entry4;
          }
          // remove excess incoming flows from nodes
          last = jmap[nodeu];
          do {
            j = nodei[last];
            curflow = arcflow[j];
            if (nodeflow[nodeu] < curflow) curflow = nodeflow[nodeu];
            arcflow[j] -= curflow;
            nodeflow[nodeu] -= curflow;
            nodev = nodej[last];
            nodeflow[nodev] += curflow;
            last++;
          } while (nodeflow[nodeu] > 0);
          nodeflow[nodeu] = -1;
          controle = true;
          continue entry4;
        }
      }
    }
  }
}


public static void minSumMatching(int n, double weight[][], int sol[])
{
  int nn,i,j,head,min,max,sub,idxa,idxc;
  int kk1,kk3,kk6,mm1,mm2,mm3,mm4,mm5;
  int index=0,idxb=0,idxd=0,idxe=0,kk2=0,kk4=0,kk5=0;
  int aux1[] = new int[n+(n/2)+1];
  int aux2[] = new int[n+(n/2)+1];
  int aux3[] = new int[n+(n/2)+1];
  int aux4[] = new int[n+1];
  int aux5[] = new int[n+1];
  int aux6[] = new int[n+1];
  int aux7[] = new int[n+1];
  int aux8[] = new int[n+1];
  int aux9[] = new int[n+1];
  double big,eps,cswk,cwk2,cst,cstlow,xcst,xwork,xwk2,xwk3,value;
  double work1[] = new double[n+1];
  double work2[] = new double[n+1];
  double work3[] = new double[n+1];
  double work4[] = new double[n+1];
  double cost[] = new double[n*(n-1)/2 + 1];
  boolean fin,skip;

  // initialization
  eps = 1.0e-5;
  fin = false;
  nn = 0;
  for (j=2; j<=n; j++)
    for (i=1; i<j; i++) {
      nn++;
      cost[nn] = weight[i][j];
    }
  big = 1.;
  for (i=1; i<=n; i++)
    big += cost[i];
  aux1[2] = 0;
  for (i=3; i<=n; i++)
    aux1[i] = aux1[i-1] + i - 2;
  head = n + 2;
  for (i=1; i<=n; i++) {
    aux2[i] = i;
    aux3[i] = i;
    aux4[i] = 0;
    aux5[i] = i;
    aux6[i] = head;
    aux7[i] = head;
    aux8[i] = head;
    sol[i] = head;
    work1[i] = big;
    work2[i] = 0.;
    work3[i] = 0.;
    work4[i] = big;
  }
  // start procedure
  for (i=1; i<=n; i++)
    if (sol[i] == head) {
      nn = 0;
      cwk2 = big;
      for (j=1; j<=n; j++) {
        min = i;
        max = j;
        if (i != j) {
          if (i > j) {
            max = i;
            min = j;
          }
          sub = aux1[max] + min;
          xcst = cost[sub];
          cswk = cost[sub] - work2[j];
          if (cswk <= cwk2) {
            if (cswk == cwk2) {
              if (nn == 0)
                if (sol[j] == head) nn = j;
              continue;
            }
            cwk2 = cswk;
            nn = 0;
            if (sol[j] == head) nn = j;
          }
        }
      }
      if (nn != 0) {
        work2[i] = cwk2;
        sol[i] = nn;
        sol[nn] = i;
      }
    }
  // initial labeling
  nn = 0;
  for (i=1; i<=n; i++)
    if (sol[i] == head) {
      nn++;
      aux6[i] = 0;
      work4[i] = 0.;
      xwk2 = work2[i];
      for (j=1; j<=n; j++) {
        min = i;
        max = j;
        if (i != j) {
          if (i > j) {
            max = i;
            min = j;
          }
          sub = aux1[max] + min;
          xcst = cost[sub];
          cswk = cost[sub] - xwk2 - work2[j];
          if (cswk < work1[j]) {
            work1[j] = cswk;
            aux4[j] = i;
          }
        }
      }
    }
  if (nn <= 1) fin = true;
  // examine the labeling and prepare for the next step
  iterate:
  while (true) {
    if (fin) {
      // generate the original graph by expanding all shrunken blossoms
      skip = false;
      value = 0.;
      for (i=1; i<=n; i++)
        if (aux2[i] == i) {
          if (aux6[i] >= 0) {
            kk5 = sol[i];
            kk2 = aux2[kk5];
            kk4 = sol[kk2];
            aux6[i] = -1;
            aux6[kk2] = -1;
            min = kk4;
            max = kk5;
            if (kk4 != kk5) {
              if (kk4 > kk5) {
                max = kk4;
                min = kk5;
              }
              sub = aux1[max] + min;
              xcst = cost[sub];
              value += xcst;
            }
          }
        }
      for (i=1; i<=n; i++) {
        while (true) {
          idxb = aux2[i];
          if (idxb == i) break;
          mm2 = aux3[idxb];
          idxd = aux4[mm2];
          kk3 = mm2;
          xwork = work4[mm2];
          do {
            mm1 = mm2;
            idxe = aux5[mm1];
            xwk2 = work2[mm1];
            while (true) {
              aux2[mm2] = mm1;
              work3[mm2] -= xwk2;
              if (mm2 == idxe) break;
              mm2 = aux3[mm2];
            }
            mm2 = aux3[idxe];
            aux3[idxe] = mm1;
          } while (mm2 != idxd);
          work2[idxb] = xwork;
          aux3[idxb] = idxd;
          mm2 = idxd;
          while (true) {
            work3[mm2] -= xwork;
            if (mm2 == idxb) break;
            mm2 = aux3[mm2];
          }
          mm5 = sol[idxb];
          mm1 = aux2[mm5];
          mm1 = sol[mm1];
          kk1 = aux2[mm1];
          if (idxb != kk1) {
            sol[kk1] = mm5;
            kk3 = aux7[kk1];
            kk3 = aux2[kk3];
            do {
              mm3 = aux6[kk1];
              kk2 = aux2[mm3];
              mm1 = aux7[kk2];
              mm2 = aux8[kk2];
              kk1 = aux2[mm1];
              sol[kk1] = mm2;
              sol[kk2] = mm1;
              min = mm1;
              max = mm2;
              if (mm1 == mm2) {
                skip = true;
                break;
              }
              if (mm1 > mm2) {
                max = mm1;
                min = mm2;
              }
              sub = aux1[max] + min;
              xcst = cost[sub];
              value += xcst;
            } while (kk1 != idxb);
            if (kk3 == idxb) skip = true;
          }
          if (skip)
            skip = false;
          else {
            while (true) {
              kk5 = aux6[kk3];
              kk2 = aux2[kk5];
              kk6 = aux6[kk2];
              min = kk5;
              max = kk6;
              if (kk5 == kk6) break;
              if (kk5 > kk6) {
                max = kk5;
                min = kk6;
              }
              sub = aux1[max] + min;
              xcst = cost[sub];
              value += xcst;
              kk6 = aux7[kk2];
              kk3 = aux2[kk6];
              if (kk3 == idxb) break;
            }
          }
        }
      }
      weight[0][0] = value;
      return;
    }
    cstlow = big;
    for (i=1; i<=n; i++)
      if (aux2[i] == i) {
        cst = work1[i];
        if (aux6[i] < head) {
          cst = 0.5 * (cst + work4[i]);
          if (cst <= cstlow) {
            index = i;
            cstlow = cst;
          }
        }
        else {
          if (aux7[i] < head) {
            if (aux3[i] != i) {
              cst += work2[i];
              if (cst < cstlow) {
                index = i;
                cstlow = cst;
              }
            }
          }
          else {
            if (cst < cstlow) {
              index = i;
              cstlow = cst;
            }
          }
        }
      }
    if (aux7[index] >= head) {
      skip = false;
      if (aux6[index] < head) {
        idxd = aux4[index];
        idxe = aux5[index];
        kk4 = index;
        kk1 = kk4;
        kk5 = aux2[idxd];
        kk2 = kk5;
        while (true) {
          aux7[kk1] = kk2;
          mm5 = aux6[kk1];
          if (mm5 == 0) break;
          kk2 = aux2[mm5];
          kk1 = aux7[kk2];
          kk1 = aux2[kk1];
        }
        idxb = kk1;
        kk1 = kk5;
        kk2 = kk4;
        while (true) {
          if (aux7[kk1] < head) break;
          aux7[kk1] = kk2;
          mm5 = aux6[kk1];
          if (mm5 == 0) {
            // augmentation of the matching
            // exchange the matching and non-matching edges 
            //   along the augmenting path
            idxb = kk4;
            mm5 = idxd;
            while (true) {
              kk1 = idxb;
              while (true) {
                sol[kk1] = mm5;
                mm5 = aux6[kk1];
                aux7[kk1] = head;
                if (mm5 == 0) break;
                kk2 = aux2[mm5];
                mm1 = aux7[kk2];
                mm5 = aux8[kk2];
                kk1 = aux2[mm1];
                sol[kk2] = mm1;
              }
              if (idxb != kk4) break;
              idxb = kk5;
              mm5 = idxe;
            }
            // remove all labels on on-exposed base nodes
            for (i=1; i<=n; i++)
              if (aux2[i] == i) {
                if (aux6[i] < head) {
                  cst = cstlow - work4[i];
                  work2[i] += cst;
                  aux6[i] = head;
                  if (sol[i] != head)
                    work4[i] = big;
                  else {
                    aux6[i] = 0;
                    work4[i] = 0.;
                  }
                }
                else {
                  if (aux7[i] < head) {
                    cst = work1[i] - cstlow;
                    work2[i] += cst;
                    aux7[i] = head;
                    aux8[i] = head;
                  }
                  work4[i] = big;
                }
                work1[i] = big;
              }
            nn -= 2;
            if (nn <= 1) {
              fin = true;
              continue iterate;
            }
            // determine the new work1 values
            for (i=1; i<=n; i++) {
              kk1 = aux2[i];
              if (aux6[kk1] == 0) {
                xwk2 = work2[kk1];
                xwk3 = work3[i];
                for (j=1; j<=n; j++) {
                  kk2 = aux2[j];
                  if (kk1 != kk2) {
                    min = i;
                    max = j;
                    if (i != j) {
                      if (i > j) {
                        max = i;
                        min = j;
                      }
                      sub = aux1[max] + min;
                      xcst = cost[sub];
                      cswk = cost[sub] - xwk2 - xwk3;
                      cswk -= (work2[kk2] + work3[j]);
                      if (cswk < work1[kk2]) {
                        aux4[kk2] = i;
                        aux5[kk2] = j;
                        work1[kk2] = cswk;
                      }
                    }
                  }
                }
              }
            }
            continue iterate;
          }
          kk2 = aux2[mm5];
          kk1 = aux7[kk2];
          kk1 = aux2[kk1];
        }
        while (true) {
          if (kk1 == idxb) {
            skip = true;
            break;
          }
          mm5 = aux7[idxb];
          aux7[idxb] = head;
          idxa = sol[mm5];
          idxb = aux2[idxa];
        }
      }
      if (!skip) {
        // growing an alternating tree, add two edges
        aux7[index] = aux4[index];
        aux8[index] = aux5[index];
        idxa = sol[index];
        idxc = aux2[idxa];
        work4[idxc] = cstlow;
        aux6[idxc] = sol[idxc];
        msmSubprogramb(idxc,n,big,cost,aux1,aux2,aux3,aux4,
                       aux5,aux7,aux9,work1,work2,work3,work4);
        continue;
      }
      skip = false;
      // shrink a blossom
      xwork = work2[idxb] + cstlow - work4[idxb];
      work2[idxb] = 0.;
      mm1 = idxb;
      do {
        work3[mm1] += xwork;
        mm1 = aux3[mm1];
      } while (mm1 != idxb);
      mm5 = aux3[idxb];
      if (idxb == kk5) {
        kk5 = kk4;
        kk2 = aux7[idxb];
      }
      while (true) {
        aux3[mm1] = kk2;
        idxa = sol[kk2];
        aux6[kk2] = idxa;
        xwk2 = work2[kk2] + work1[kk2] - cstlow;
        mm1 = kk2;
        do {
          mm2 = mm1;
          work3[mm2] += xwk2;
          aux2[mm2] = idxb;
          mm1 = aux3[mm2];
        } while (mm1 != kk2);
        aux5[kk2] = mm2;
        work2[kk2] = xwk2;
        kk1 = aux2[idxa];
        aux3[mm2] = kk1;
        xwk2 = work2[kk1] + cstlow - work4[kk1];
        mm2 = kk1;
        do {
          mm1 = mm2;
          work3[mm1] += xwk2;
          aux2[mm1] = idxb;
          mm2 = aux3[mm1];
        } while (mm2 != kk1);
        aux5[kk1] = mm1;
        work2[kk1] = xwk2;
        if (kk5 != kk1) {
          kk2 = aux7[kk1];
          aux7[kk1] = aux8[kk2];
          aux8[kk1] = aux7[kk2];
          continue;
        }
        if (kk5 != index) {
          aux7[kk5] = idxe;
          aux8[kk5] = idxd;
          if (idxb != index) {
            kk5 = kk4;
            kk2 = aux7[idxb];
            continue;
          }
        }
        else {
          aux7[index] = idxd;
          aux8[index] = idxe;
        }
        break;
      }
      aux3[mm1] = mm5;
      kk4 = aux3[idxb];
      aux4[kk4] = mm5;
      work4[kk4] = xwork;
      aux7[idxb] = head;
      work4[idxb] = cstlow;
      msmSubprogramb(idxb,n,big,cost,aux1,aux2,aux3,aux4,
                     aux5,aux7,aux9,work1,work2,work3,work4);
      continue iterate;
    }
    // expand a t-labeled blossom
    kk4 = aux3[index];
    kk3 = kk4;
    idxd = aux4[kk4];
    mm2 = kk4;
    do {
      mm1 = mm2;
      idxe = aux5[mm1];
      xwk2 = work2[mm1];
      while (true) {
        aux2[mm2] = mm1;
        work3[mm2] -= xwk2;
        if (mm2 == idxe) break;
        mm2 = aux3[mm2];
      }
      mm2 = aux3[idxe];
      aux3[idxe] = mm1;
    } while (mm2 != idxd);
    xwk2 = work4[kk4];
    work2[index] = xwk2;
    aux3[index] = idxd;
    mm2 = idxd;
    while (true) {
      work3[mm2] -= xwk2;
      if (mm2 == index) break;
      mm2 = aux3[mm2];
    }
    mm1 = sol[index];
    kk1 = aux2[mm1];
    mm2 = aux6[kk1];
    idxb = aux2[mm2];
    if (idxb != index) {
      kk2 = idxb;
      while (true) {
        mm5 = aux7[kk2];
        kk1 = aux2[mm5];
        if (kk1 == index) break;
        kk2 = aux6[kk1];
        kk2 = aux2[kk2];
      }
      aux7[idxb] = aux7[index];
      aux7[index] = aux8[kk2];
      aux8[idxb] = aux8[index];
      aux8[index] = mm5;
      mm3 = aux6[idxb];
      kk3 = aux2[mm3];
      mm4 = aux6[kk3];
      aux6[idxb] = head;
      sol[idxb] = mm1;
      kk1 = kk3;
      while (true) {
        mm1 = aux7[kk1];
        mm2 = aux8[kk1];
        aux7[kk1] = mm4;
        aux8[kk1] = mm3;
        aux6[kk1] = mm1;
        sol[kk1] = mm1;
        kk2 = aux2[mm1];
        sol[kk2] = mm2;
        mm3 = aux6[kk2];
        aux6[kk2] = mm2;
        if (kk2 == index) break;
        kk1 = aux2[mm3];
        mm4 = aux6[kk1];
        aux7[kk2] = mm3;
        aux8[kk2] = mm4;
      }
    }
    mm2 = aux8[idxb];
    kk1 = aux2[mm2];
    work1[kk1] = cstlow;
    kk4 = 0;
    skip = false;
    if (kk1 != idxb) {
      mm1 = aux7[kk1];
      kk3 = aux2[mm1];
      aux7[kk1] = aux7[idxb];
      aux8[kk1] = mm2;
      do {
        mm5 = aux6[kk1];
        aux6[kk1] = head;
        kk2 = aux2[mm5];
        mm5 = aux7[kk2];
        aux7[kk2] = head;
        kk5 = aux8[kk2];
        aux8[kk2] = kk4;
        kk4 = kk2;
        work4[kk2] = cstlow;
        kk1 = aux2[mm5];
        work1[kk1] = cstlow;
      } while (kk1 != idxb);
      aux7[idxb] = kk5;
      aux8[idxb] = mm5;
      aux6[idxb] = head;
      if (kk3 == idxb) skip = true;
    }
    if (skip) 
      skip = false;
    else {
      kk1 = 0;
      kk2 = kk3;
      do {
        mm5 = aux6[kk2];
        aux6[kk2] = head;
        aux7[kk2] = head;
        aux8[kk2] = kk1;
        kk1 = aux2[mm5];
        mm5 = aux7[kk1];
        aux6[kk1] = head;
        aux7[kk1] = head;
        aux8[kk1] = kk2;
        kk2 = aux2[mm5];
      } while (kk2 != idxb);
      msmSubprograma(kk1,n,big,cost,aux1,aux2,aux3,aux4,aux5,
                     aux6,aux8,work1,work2,work3,work4);
    }
    while (true) {
      if (kk4 == 0) continue iterate;
      idxb = kk4;
      msmSubprogramb(idxb,n,big,cost,aux1,aux2,aux3,aux4,
                     aux5,aux7,aux9,work1,work2,work3,work4);
      kk4 = aux8[idxb];
      aux8[idxb] = head;
    }
  }
}


public static void minimumSpanningTreeKruskal(int n, int m, int nodei[], 
              int nodej[], int weight[], int treearc1[], int treearc2[])
{
  int i,index,index1,index2,index3,nodeu,nodev,nodew,len,nedge,treearc;
  int halfm,numarc,nedge2=0;
  int predecessor[] = new int[n+1];

  for (i=1; i<=n; i++)
    predecessor[i] = -1;
  // initialize the heap structure
  i = m / 2;
  while (i > 0) {
    index1 = i;
    halfm = m / 2;
    while (index1 <= halfm) {
      index = index1 + index1;
      index2 = ((index < m) && (weight[index + 1] < weight[index])) ?
               index + 1 : index;
      if (weight[index2] < weight[index1]) {
        nodeu = nodei[index1];
        nodev = nodej[index1];
        len   = weight[index1];
        nodei[index1] = nodei[index2];
        nodej[index1] = nodej[index2];
        weight[index1] = weight[index2];
        nodei[index2] = nodeu;
        nodej[index2] = nodev;
        weight[index2] = len;
        index1 = index2;
      }
      else
        index1 = m;
    }
    i--;
  }
  nedge = m;
  treearc = 0;
  numarc = 0;
  while ((treearc < n-1) && (numarc < m)) {
    // examine the next edge
    numarc++;
    nodeu = nodei[1];
    nodev = nodej[1];
    nodew = nodeu;
    // check if nodeu and nodev are in the same component
    while (predecessor[nodew] > 0) {
      nodew = predecessor[nodew];
    }
    index1 = nodew;
    nodew = nodev;
    while (predecessor[nodew] > 0) {
      nodew = predecessor[nodew];
    }
    index2 = nodew;
    if (index1 != index2) {
      // include nodeu and nodev in the minimum spanning tree
      index3 = predecessor[index1] + predecessor[index2];
      if (predecessor[index1] > predecessor[index2]) {
        predecessor[index1] = index2;
        predecessor[index2] = index3;
      }
      else {
        predecessor[index2] = index1;
        predecessor[index1] = index3;
      }
      treearc++;
      treearc1[treearc] = nodeu;
      treearc2[treearc] = nodev;
    }
    // restore the heap structure
    nodei[1] = nodei[nedge];
    nodej[1] = nodej[nedge];
    weight[1] = weight[nedge];
    nedge--;
    index1 = 1;
    nedge2 = nedge / 2;
    while (index1 <= nedge2) {
      index = index1 + index1;
      index2 = ((index < nedge) && (weight[index + 1] < weight[index])) ?
               index + 1 : index;
      if (weight[index2] < weight[index1]) {
        nodeu = nodei[index1];
        nodev = nodej[index1];
        len   = weight[index1];
        nodei[index1] = nodei[index2];
        nodej[index1] = nodej[index2];
        weight[index1] = weight[index2];
        nodei[index2] = nodeu;
        nodej[index2] = nodev;
        weight[index2] = len;
        index1 = index2;
      }
      else
        index1 = nedge;
    }
  }
  treearc1[0] = treearc;
}


public static void minimumSpanningTreePrim(int n, int dist[][], int tree[])
{
  int i,j,n1,d,mindist,node,k=0;

  n1 = n - 1;
  for (i=1; i<=n1; i++)
    tree[i] = -n;
  tree[n] = 0;
  for (i=1; i<=n1; i++) {
    mindist = Integer.MAX_VALUE;
    for (j=1; j<=n1; j++) {
      node = tree[j];
      if (node <= 0) {
        d = dist[-node][j];
        if (d < mindist) {
          mindist = d;
          k = j;
        }
      }
    }
    tree[k] = -tree[k];
    for (j=1; j<=n1; j++) {
      node = tree[j];
      if (node <= 0)
        if (dist[j][k] < dist[j][-node]) tree[j] = -k;
    }
  }
}


public static void multipleKnapsack(int n, int m, int profit[], int weight[],
                                    int capacity[], int depth, int sol[])
{
  int i, i1, p, idx, idx1, idx2, j, y, tmp, netp, slackbnd, res1a, ubtmp;
  int minweight, totalweight, depthtmp, n1, m1, proftmp, ubslack;
  int q=0, upperbnd=0, indexj=0, indexi=0;
  float r;
  float pwratio[] = new float[n + 1];
  int res1[] = new int[1];
  int res2[] = new int[1];
  int res3[] = new int[1];
  int res4[] = new int[1];
  int totalprofit[] = new int[1];
  int origp[] = new int[n + 1];
  int origw[] = new int[n + 1];
  int origindex[] = new int[n + 1];
  int aux0[] = new int[m + 1];
  int aux1[] = new int[m + 1];
  int aux2[] = new int[m + 1];
  int aux3[] = new int[m + 1];
  int aux4[] = new int[n + 1];
  int aux5[] = new int[n + 1];
  int aux6[] = new int[n + 1];
  int aux7[] = new int[n + 1];
  int aux8[] = new int[n + 1];
  int aux9[] = new int[n + 2];
  int aux10[] = new int[n + 2];
  int aux11[] = new int[n + 2];
  int aux12[][] = new int[m+1][n + 1];
  int aux13[][] = new int[m+1][n+1];
  int aux14[][] = new int[m+1][n+1];
  int aux15[][] = new int[m+1][n+2];
  boolean control[] = new boolean[1];
  boolean skip=false, outer=false;
  boolean unmark[] = new boolean[n + 1];

  // check for invalid input data
  totalprofit[0] = 0;
  if (n <= 1) totalprofit[0] = - 1;
  if (m <= 0) totalprofit[0] = - 1;
  if (totalprofit[0] < 0) {
    sol[0] = totalprofit[0]; 
    return;
  }
  minweight = weight[1];
  totalweight = weight[1];
  if (profit[1] <= 0) totalprofit[0] = -2;
  if (weight[1] <= 0) totalprofit[0] = -2;
  for (j=2; j<=n; j++) {
    if (profit[j] <= 0) totalprofit[0] = -2;
    if (weight[j] <= 0) totalprofit[0] = -2;
    if (weight[j] < minweight) minweight = weight[j];
    totalweight += weight[j];
  }

  // store the original input
  for (j=1; j<=n; j++) {
    origp[j] = profit[j];
    origw[j] = weight[j];
    pwratio[j] = ((float) profit[j]) / ((float) weight[j]);
    unmark[j] = true;
  }
     
  // sort the input
  for (i=1; i<=n; i++) {
    r = -1.0F;
    for (j=1; j<=n; j++)
      if (unmark[j]) {
        if (pwratio[j] > r) {
          r = pwratio[j];
          q = j;
        }
      }
    unmark[q] = false;
    profit[i] = origp[q];
    weight[i] = origw[q];
    origindex[i] = q;
  }

  if (capacity[1] <= 0) totalprofit[0] = -2;
  if (m == 1) {
    // solve the special case of one knapsack problem
    if (minweight > capacity[1]) totalprofit[0] = -3;
    if (totalweight <= capacity[1]) {
      // the knapsacks contain all the items
      q = 0;
      for (j=1; j<=n; j++) {
        profit[j] = origp[j];
        weight[j] = origw[j];
        sol[j] = 1;
        q += origw[j];
      }
      sol[0] = q;
      return;
    }
    if (totalprofit[0] < 0) {
      sol[0] = totalprofit[0]; 
       for (j=1; j<=n; j++) {
        profit[j] = origp[j];
        weight[j] = origw[j];
      }
     return;
    }
    res4[0] = capacity[1];
    for (j=1; j<=n; j++) {
      aux10[j] = profit[j];
      aux11[j] = weight[j];
    }
    // compute the solution with one knapsack
    mkpsSingleKnapsack(n,n,res4,0,totalprofit,aux8,aux10,aux11);
    depth = 0;
    sol[0] = totalprofit[0];
    for (j=1; j<=n; j++) {
      profit[j] = origp[j];
      weight[j] = origw[j];
      sol[origindex[j]] = aux8[j];
    }
    return;
  }
  for (i=2; i<=m; i++) {
    if (capacity[i] <= 0) totalprofit[0] = -2;
    if (capacity[i] < capacity[i-1]) {
      sol[0] = -4;
      for (j=1; j<=n; j++) {
        profit[j] = origp[j];
        weight[j] = origw[j];
      }
      return;
    }
  }
  if (minweight > capacity[1]) totalprofit[0] = -3;
  if (totalweight <= capacity[m]) totalprofit[0] = -4;
  if (totalprofit[0] < 0) {
    sol[0] = totalprofit[0]; 
    for (j=1; j<=n; j++) {
      profit[j] = origp[j];
      weight[j] = origw[j];
    }
    return;
  }

  // initialize
  depthtmp = depth;
  depth = 0;
  netp = 0;
  n1 = n + 1;
  aux9[n1] = 1;
  m1 = m - 1;
  for (j=1; j<=n; j++) {
    aux9[j] = 1;
    for (i=1; i<=m; i++) {
      aux13[i][j] = 0;
      aux12[i][j] = 0;
    }
  }
  for (i=1; i<=m1; i++) {
    aux2[i] = capacity[i];
    aux0[i] = -1;
  }
  aux2[m] = capacity[m];
  totalprofit[0] = 0;
  slackbnd = 0;
  idx = 1;
  // compute an upper bound of the current solution
  mkpsCurrentUpperBound(n,m,profit,weight,capacity,1,netp,res1,res3,
        aux5,aux7,aux8,aux9,aux10,aux11);
  for (j=1; j<=n; j++)
    aux6[j] = aux5[j];
  res1a = res1[0];
  ubtmp = res3[0];
  control[0] = false;

  while (true) {
    // using heuristic approximation
    outer = false;
    netp = totalprofit[0] - slackbnd;
    // get a feasible solution
    mkpsFeasibleSolution(n,m,profit,weight,idx,netp,res2,aux1,aux2,
                 aux3,aux7,aux8,aux9,aux10,aux11,aux12,aux14,aux15);
    if (res2[0] + slackbnd > totalprofit[0]) {
      totalprofit[0] = res2[0] + slackbnd;
      for (j=1; j<=n; j++) {
        sol[j] = 0;
        for (y=1; y<=idx; y++)
          if (aux13[y][j] != 0) {
            sol[j] = y;
            break;
          }
      }
      idx1 = aux1[i];
      if (idx1 != 0) {
        for (j=1; j<=idx1; j++) {
          q = aux15[idx][j];
          if (aux14[idx][j] == 1) sol[q] = idx;
        }
      }
      i1 = idx + 1;
      for (p=i1; p<=m; p++) {
        idx1 = aux1[p];
        if (idx1 != 0)
          for (j=1; j<=idx1; j++) {
            q = aux15[p][j];
            if (aux14[p][j] == 1) sol[q] = p;
          }
      }
      if (res3[0] == res2[0]) {
        outer = true;
      }
    }
    if (!outer) {
      skip = false;
      while (true) {
        if (aux3[idx] != 0) {
          // update
          ubslack = res3[0] + slackbnd;
          tmp = aux1[idx];
          proftmp = 0;
          for (y=1; y<=tmp; y++) {
            if (aux14[idx][y] != 0) {
              j = aux15[idx][y];
              aux13[idx][j] = 1;
              aux2[idx] -= weight[j];
              slackbnd += profit[j];
              aux9[j] = 0;
              aux12[idx][j] = aux0[idx];
              aux4[j] = ubslack;
              if (!control[0]) {
                upperbnd = ubslack;
                indexj = j;
                indexi = idx;
              }
              aux0[idx] = j;
              proftmp += profit[j];
              if (proftmp == aux3[idx]) {
                skip = true;
                break;
              }
              // upper bound computation
              mkpsCalculateBound(idx,idx,res3,control,slackbnd,upperbnd,
                                 indexj,indexi,n,res1,res1a,ubtmp,aux0,
                                 aux2,aux5,aux6,aux9,aux12);
              if (!control[0]) {
                netp = totalprofit[0] - slackbnd;
                // compute an upper bound of the current solution
                mkpsCurrentUpperBound(n,m,profit,weight,aux2,idx,netp,
                            res1,res3,aux5,aux7,aux8,aux9,aux10,aux11);
                indexj = n1;
              }
              ubslack = res3[0] + slackbnd;
              if (ubslack <= totalprofit[0]) {
                outer = true;
                break;
              }
            }
          }
          if (skip || outer) break;
        }
        if (idx == m - 1) {
          outer = true;
          break;
        }
        idx2 = idx + 1;
        // upper bound computation
        mkpsCalculateBound(idx2,idx,res3,control,slackbnd,upperbnd,indexj,
                indexi,n,res1,res1a,ubtmp,aux0,aux2,aux5,aux6,aux9,aux12);
        if (!control[0]) {
          netp = totalprofit[0] - slackbnd;
          // compute an upper bound of the current solution
          mkpsCurrentUpperBound(n,m,profit,weight,aux2,idx2,netp,res1,
                                res3,aux5,aux7,aux8,aux9,aux10,aux11);
          indexj = n1;
        }
        if (res3[0] + slackbnd <= totalprofit[0]) {
          outer = true;
          break;
        }
        idx++;
      }
    }
    while (true) {
      // backtrack
      if (idx <= 0) {
        depth--;
        sol[0] = totalprofit[0];
        for (j=1; j<=n; j++)
          aux8[j] = sol[j];
        for (j=1; j<=n; j++) {
          profit[j] = origp[j];
          weight[j] = origw[j];
          sol[origindex[j]] = aux8[j];
        }
        return;
      }
      if (depth == depthtmp) {
        sol[0] = totalprofit[0];
        for (j=1; j<=n; j++)
          aux8[j] = sol[j];
        for (j=1; j<=n; j++) {
          profit[j] = origp[j];
          weight[j] = origw[j];
          sol[origindex[j]] = aux8[j];
        }
        return;
      }
      depth++;
      if (aux0[idx] == -1) {
        for (j=1; j<=n; j++)
          aux12[idx][j] = 0;
        idx--;
        continue;
      }
      j = aux0[idx];
      aux13[idx][j] = 0;
      aux9[j] = 1;
      slackbnd -= profit[j];
      aux2[idx] += weight[j];
      for (y=1; y<=n; y++)
        if (aux12[idx][y] == j) aux12[idx][y] = 0;
      aux0[idx] = aux12[idx][j];
      if (aux4[j] > totalprofit[0]) break;
    }
    res3[0] = aux4[j] - slackbnd;
    control[0] = true;
  }
}


public static void nodeColoring(int n, int m, int nodei[],
                                int nodej[], int color[])
{
  int i,j,k,loop,currentnode,newc,ncolor,paint,index,nodek,up,low;
  int degree[] = new int[n+1];
  int choices[] = new int[n+1];
  int maxcolornum[] = new int[n+1];
  int currentcolor[] = new int[n+1];
  int feasiblecolor[] = new int[n+1];
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+m+1];
  int availc[][] = new int[n+1][n+1];
  boolean more;

  // set up the forward star representation of the graph
  for (i=1; i<=n; i++)
    degree[i] = 0;
  k = 0;
  for (i=1; i<=n; i++) {
    firstedges[i] = k + 1;
    for (j=1; j<=m; j++)
      if (nodei[j] == i) {
        k++;
        endnode[k] = nodej[j];
        degree[i]++;
      }
      else {
        if (nodej[j] == i) {
          k++;
          endnode[k] = nodei[j];
          degree[i]++;
        }
      }
  }
  firstedges[n+1] = k + 1;
  for (i=1; i<=n; i++) {
    feasiblecolor[i] = degree[i] + 1;
    if (feasiblecolor[i] > i) feasiblecolor[i] = i;
    choices[i] = feasiblecolor[i];
    loop = feasiblecolor[i];
    for (j=1; j<=loop; j++)
      availc[i][j] = n;
    k = feasiblecolor[i] + 1;
    if (k <= n)
      for (j=k; j<=n; j++)
        availc[i][j] = 0;
  }
  currentnode = 1;
  // color currentnode
  newc = 1;
  ncolor = n;
  paint = 0;
  more = true;
  do {
    if (more) {
      index = choices[currentnode];
      if (index > paint + 1) index = paint + 1;
      while ((availc[currentnode][newc] < currentnode) && (newc <= index))
        newc++;
      // currentnode has the color 'newc'
      if (newc == index + 1)
        more = false;
      else {
        if (currentnode == n) {
          // a new coloring is found
          currentcolor[currentnode] = newc;
          for (i=1; i<=n; i++)
            color[i] = currentcolor[i];
          if (newc > paint) paint++;
          ncolor = paint;
          if (ncolor > 2) {
          // backtrack to the first node of color 'ncolor'
            index = 1;
            while (color[index] != ncolor)
              index++;
            j = n;
            while (j >= index) {
              currentnode--;
              newc = currentcolor[currentnode];
              paint = maxcolornum[currentnode];
              low = firstedges[currentnode];
              up = firstedges[currentnode + 1];
              if (up > low) {
                up--;
                for (k=low; k<=up; k++) {
                  nodek = endnode[k];
                  if (nodek > currentnode)
                    if (availc[nodek][newc] == currentnode) {
                      availc[nodek][newc] = n;
                      feasiblecolor[nodek]++;
                    }
                }
              }
              newc++;
              more = false;
              j--;
            }
            paint = ncolor - 1;
            for (i=1; i<=n; i++) {
              loop = choices[i];
              if (loop > paint) {
                k = paint + 1;
                for (j=k; j<=loop; j++)
                  if (availc[i][j] == n) feasiblecolor[i]--;
                choices[i] = paint;
              }
            }
          }
        }
        else {
          // currentnode is less than n
          low = firstedges[currentnode];
          up = firstedges[currentnode + 1];
          if (up > low) {
            up--;
            k = low;
            while ((k <= up) && more) {
              nodek = endnode[k];
              if (nodek > currentnode)
                more = !((feasiblecolor[nodek] == 1) &&
                       (availc[nodek][newc] >= currentnode));
              k++;
            }
          }
          if (more) {
            currentcolor[currentnode] = newc;
            maxcolornum[currentnode] = paint;
            if (newc > paint) paint++;
            low = firstedges[currentnode];
            up = firstedges[currentnode + 1];
            if (up > low) {
              up--;
              for (k=low; k<=up; k++) {
                nodek = endnode[k];
                if (nodek > currentnode)
                  if (availc[nodek][newc] >= currentnode) {
                    availc[nodek][newc] = currentnode;
                    feasiblecolor[nodek]--;
                  }
              }
            }
            currentnode++;
            newc = 1;
          }
          else
            newc++;
        }
      }
    }
    else {  
      more = true;
      if ((newc > choices[currentnode]) || (newc > paint + 1)) {
        currentnode--;
        newc = currentcolor[currentnode];
        paint = maxcolornum[currentnode];
        low = firstedges[currentnode];
        up = firstedges[currentnode + 1];
        if (up > low) {
          up--;
          for (k=low; k<=up; k++) {
            nodek = endnode[k];
            if (nodek > currentnode)
              if (availc[nodek][newc] == currentnode) {
                availc[nodek][newc] = n;
                feasiblecolor[nodek]++;
              }
          }
        }
        newc++;
        more = false;
      }
    }
  } while ((currentnode != 1) && (ncolor != 2));
  color[0] = ncolor;
}


public static int minCostNetworkFlow(int nodes, int edges, int numdemand,
              int nodedemand[][], int nodei[], int nodej[], int arccost[],
              int upbound[], int lowbound[], int arcsol[][], int flowsol[])
{
  int i, j, k, l, m, n, lastslackedge, solarc, temp, tmp, u, v, remain, rate;
  int arcnam, tedges, tedges1, nodes1, nodes2, nzdemand, value, valuez;
  int tail, ratez, tailz, trial, distdiff, olddist, treenodes, iterations;
  int right, point, part, jpart, kpart, spare, sparez, lead, otherend, sedge;
  int orig, load, curedge, p, q, r, vertex1, vertex2, track, spointer, focal;
  int newpr, newlead, artedge, maxint, artedge1, ipart, distlen;
  int after=0, other=0, left=0, newarc=0, newtail=0;
  int pred[] = new int[nodes + 2];
  int succ[] = new int[nodes + 2];
  int dist[] = new int[nodes + 2];
  int sptpt[] = new int[nodes + 2];
  int flow[] = new int[nodes + 2];
  int dual[] = new int[nodes + 2];
  int arcnum[] = new int[nodes + 1];
  int head[] = new int[edges * 2];
  int cost[] = new int[edges * 2];
  int room[] = new int[edges * 2];
  int least[] = new int[edges * 2];
  int rim[] = new int[3];
  int ptr[] = new int[3]; 
  boolean infeasible;
  boolean flowz=false, newprz=false, artarc=false, removelist=false;
  boolean partz=false, ipartout=false, newprnb=false;
                                                             
  for (p=0; p<=nodes; p++)
    arcnum[p] = 0;
  maxint = 0;
  for (p=1; p<=edges; p++) {
    arcnum[nodej[p]]++;
    if (arccost[p] > 0) maxint += arccost[p];
    if (upbound[p] > 0) maxint += upbound[p];
  }
  artedge = 1;
  artedge1 = artedge + 1;
  tedges =  (edges * 2) - 2;
  tedges1 = tedges + 1;
  nodes1 = nodes + 1;
  nodes2 = nodes + 2;
  dual[nodes1] = 0;
  for (p=1; p<=nodes1; p++) {
    pred[p] = 0;
    succ[p] = 0;
    dist[p] = 0;
    sptpt[p] = 0;
    flow[p] = 0;
  }
  head[artedge] = nodes1;
  cost[artedge] = maxint;
  room[artedge] = 0;
  least[artedge] = 0;
  remain = 0;
  nzdemand = 0;
  sedge = 0;

  // initialize supply and demand lists
  succ[nodes1] = nodes1;
  pred[nodes1] = nodes1;
  for (p=1; p<=numdemand; p++) {
    flow[nodedemand[p][0]] = nodedemand[p][1];
    remain += nodedemand[p][1];
    if (nodedemand[p][1] <= 0) continue;
    nzdemand++;
    dist[nodedemand[p][0]] = nodedemand[p][1];
    succ[nodedemand[p][0]] = succ[nodes1];
    succ[nodes1] = nodedemand[p][0];
  }
  if (remain < 0)  return 1;
  for (p=1; p<=nodes; p++)
    dual[p] = arcnum[p];
  i = 1;
  j = artedge;
  for (p=1; p<=nodes; p++) {
    i = -i;
    tmp = Math.max(1, dual[p]);
    if (j + tmp > tedges) return 2;
    dual[p] = (i >= 0 ? j+1 : -(j+1));
    for (q=1; q<=tmp; q++) {
      j++;
      head[j] = (i >= 0 ? p : -p);
      cost[j] = 0;
      room[j] = -maxint;
      least[j] = 0;
    }
  }

  // check for valid input data
  sedge = j + 1;
  if (sedge > tedges)  return 2;
  head[sedge] = (-i >= 0 ? nodes1 : -nodes1);
  valuez = 0;
  for (p=1; p<=edges; p++) {
    if ((nodei[p] > nodes) || (nodej[p] > nodes) || (upbound[p] >= maxint))
      return 3;
    if (upbound[p] == 0) upbound[p] = maxint;
    if (upbound[p] < 0) upbound[p] = 0;
    if ((lowbound[p] >= maxint) || (lowbound[p] < 0) || 
                        (lowbound[p] > upbound[p]))
      return 3;
    u = dual[nodej[p]];
    v = Math.abs(u);
    temp = (u >= 0 ? nodes1 : -nodes1);
    if ((temp ^ head[v]) <= 0) {
      sedge++;
      tmp = sedge - v;
      r = sedge;
      for (q=1; q<=tmp; q++) {
        temp = r - 1;
        head[r]  = head[temp];
        cost[r]  = cost[temp];
        room[r] = room[temp];
        least[r] = least[temp];
        r = temp;
      }
      for (q=nodej[p]; q<=nodes; q++)
        dual[q] += (dual[q] >= 0 ? 1 : -1);
    }

    // insert new edge
    head[v] = (u >= 0 ? nodei[p] : -nodei[p]);
    cost[v] = arccost[p];
    valuez += arccost[p] * lowbound[p];
    room[v] = upbound[p] - lowbound[p];
    least[v] = lowbound[p];
    flow[nodei[p]] -= lowbound[p];
    flow[nodej[p]] += lowbound[p];
    dual[nodej[p]] = (u >= 0 ? v+1 : -(v+1));
    sptpt[nodei[p]] = -1;
  }
  i = nodes1;
  k = artedge;
  l = 0;
  sedge--;
  for (p=artedge1; p<=sedge; p++) {
    j = head[p];
    if ((i ^ j) <= 0) {
      i = -i;
      l++;
      dual[l] = k + 1;
    }
    else
     if (Math.abs(j) == l) continue;
    k++;
    if (k != p) {
      head[k]  = head[p];
      cost[k]  = cost[p];
      room[k]  = room[p];
      least[k] = least[p];
    }
  }
  sedge = k;
  if (sedge + Math.max(1,nzdemand) + 1 > tedges) return 2;
  // add regular slacks
  i = -head[sedge];
  focal = succ[nodes1];
  succ[nodes1] = nodes1;
  if (focal == nodes1) {
    sedge++;
    head[sedge]  = (i >= 0 ? nodes1 : -nodes1);  
    cost[sedge]  = 0;
    room[sedge]  = -maxint;
    least[sedge] = 0;
  }
  else
    do {
      sedge++;
      head[sedge] = (i >= 0 ? focal : -focal);
      cost[sedge] = 0;
      room[sedge] = dist[focal];
      dist[focal] = 0;
      least[sedge] = 0;
      after = succ[focal];
      succ[focal] = 0;
      focal = after;
     } while (focal != nodes1);
  lastslackedge = sedge;
  sedge++;
  head[sedge] = (-i >= 0 ? nodes2 : -nodes2);
  cost[sedge] = maxint;
  room[sedge] = 0;
  least[sedge] = 0;
  // locate sources and sinks
  remain = 0;
  treenodes = 0;
  focal = nodes1;
  for (p=1; p<=nodes; p++) {
    j = flow[p];
    remain += j;
    if (j == 0) continue;
    if (j < 0) {
      flow[p] = -j;
      right = nodes1;
      do {
        after = pred[right];
        if (flow[after]+j <= 0) break;
        right = after;
      } while (true);
      pred[right] = p;
      pred[p] = after;
      dist[p] = -1;
    }
    else {
      treenodes++;
      sptpt[p] = -sedge;
      flow[p] = j;
      succ[focal] = p;
      pred[p] = nodes1;
      succ[p] = nodes1;
      dist[p] = 1;
      dual[p] = maxint;
      focal = p;
    }
  }
  if (remain < 0) return 4;
  do {
    // select highest rank demand
    tail = pred[nodes1];
    if (tail == nodes1) break;
    do {
      // set link to artificial
      newarc = artedge;
      newpr = maxint;
      newprz = false;
      flowz = false;
      if (flow[tail] == 0) {
        flowz = true;
        break;
      }
      // look for sources
      trial = dual[tail];
      lead = head[trial];
      other = (lead >= 0 ? nodes1 : -nodes1);
      do {
        if (room[trial] > 0) {
          orig = Math.abs(lead);
          if (dist[orig] == 1) {
            if (sptpt[orig] != artedge) {
              rate = cost[trial];
              if (rate < newpr) {
                if (room[trial] <= flow[tail]) {
                  if (flow[orig] >= room[trial]) {
                    newarc = -trial;
                    newpr = rate;
                    if (newpr == 0) {
                      newprz = true;
                      break;
                    }
                  }
                }
                else {
                  if (flow[orig] >= flow[tail]) {
                    newarc = trial;
                    newpr = rate;
                    if (newpr == 0) {
                      newprz = true;
                      break;
                    }
                  }
                }
              }
            }
          }
        }
        trial++;
        lead = head[trial];
      } while ((lead ^ other) > 0);
      if (!newprz) {
        artarc = false;
        if (newarc == artedge) {
          artarc = true;
          break;
        }
      } else
        newprz = false;
      if (newarc > 0) break;
      newarc = -newarc;
      orig = Math.abs(head[newarc]);
      load = room[newarc];
      // mark unavailable
      room[newarc] = -load;
      // adjust flows
      flow[orig] -= load;
      flow[tail] -= load;
    } while (true);
    if (!flowz) {
      removelist = false;
      if (!artarc) {
        room[newarc] = -room[newarc];
        orig = Math.abs(head[newarc]);
        flow[orig] -= flow[tail];
        k = maxint;
        removelist = true;
      }
      else {
        // search for transshipment nodes
        artarc = false;
        trial = dual[tail];
        lead = head[trial];
        newprz = false;
        do {
          if (room[trial] > 0) {
            orig = Math.abs(lead);
            // is it linked
            if (dist[orig] == 0) {
              rate = cost[trial];
              if (rate < newpr) {
                newarc = trial;
                newpr = rate;
                if (newpr == 0) {
                  newprz = true;
                  break;
                }
              }
            }
          }
          trial++;
          lead = head[trial];
        } while ((lead ^ other) > 0);
        artarc = false;
        if (!newprz) {
          if(newarc == artedge)
            artarc = true;
        }
        else
          newprz = false;
        if (!artarc) {
          orig = Math.abs(head[newarc]);
          if (room[newarc] <= flow[tail]) {
            // get capacity
            load = room[newarc];
            // mark unavailable
            room[newarc] = -load;
            // adjust flows
            flow[orig] = load;                                             
            flow[tail] -= load;
            pred[orig] = tail;
            pred[nodes1] = orig;
            dist[orig] = -1;
            continue;
          }
          // mark unavailable
          room[newarc] = -room[newarc];
          flow[orig] = flow[tail];
          pred[orig] = pred[tail];
          pred[tail] = orig;
          pred[nodes1] = orig;
          succ[orig] = tail;
          sptpt[tail] = newarc;  
          dist[orig] = dist[tail] - 1;
          dual[tail] = newpr;
          treenodes++;
          continue;
        }
        else
          artarc = false;
      }
    }
    flowz = false;
    if (!removelist)
      k = 0;
    else
      removelist = false;
    pred[nodes1] = pred[tail];
    orig = Math.abs(head[newarc]);
    sptpt[tail] = newarc;
    dual[tail] = newpr;
    pred[tail] = orig;                                                     
    i = succ[orig];
    succ[orig] = tail;
    j = dist[orig] - dist[tail] + 1;
    focal = orig;
    do {
      // adjust dual variables
      focal = succ[focal];
      l = dist[focal];
      dist[focal] = l + j;
      k -= dual[focal];
      dual[focal] = k;
    } while (l != -1);
    succ[focal] = i;
    treenodes++;
  }  while (true);

  // set up the expand tree
  tail = 1;
  trial = artedge1;
  lead = head[trial];
  do {
    if (treenodes == nodes) break;
    tailz = tail;
    newpr = maxint;
    do {
      // search for least cost connectable edge
      otherend = dist[tail];
      other = (lead >= 0 ? nodes1 : -nodes1);
      do {
        if (room[trial] > 0) {
          m = cost[trial];
          if (newpr >= m) {
            orig = Math.abs(lead);
            if (dist[orig] != 0) {
              if (otherend == 0) {
                i = orig;
                j = tail;
                k = m;
                l = trial;
                newpr = m;
              }
            }
            else {
              if (otherend != 0) {
                i = tail;
                j = orig;
                k = -m;
                l = -trial;
                newpr = m;
              }
            }
          }
        }
        trial++;
        lead = head[trial];
      } while ((lead ^ other) > 0);
      // prepare the next 'tail' group
      tail++;
      if (tail == nodes1) {
        tail = 1;
        trial = artedge1;
        lead = head[trial];
      }
      newprnb = false;
      if (newpr != maxint) {
        newprnb = true;
        break;
      }
    } while (tail != tailz);
    if (!newprnb) {
      for (p=1; p<=nodes; p++) {
        if (dist[p] != 0) continue;
        // add artificial
        sptpt[p] = artedge;
        flow[p] = 0;
        succ[p] = succ[nodes1];
        succ[nodes1] = p;
        pred[p] = nodes1;
        dist[p] = 1;
        dual[p] = -maxint;
      }
      break;
    }
    newprnb = false;
    sptpt[j] = l;
    pred[j] = i;
    succ[j] = succ[i];
    succ[i] = j;
    dist[j] = dist[i] + 1;
    dual[j] = dual[i] - k;
    newarc = Math.abs(l);
    room[newarc] = -room[newarc];
    treenodes++;
  } while (true);
  for (p=1; p<=nodes; p++) {
    q = Math.abs(sptpt[p]);
    room[q] = -room[q];
  }
  for (p=1; p<=sedge; p++)
    if (room[p] + maxint == 0)  room[p] = 0;
  room[artedge] = maxint;
  room[sedge] = maxint;

  // initialize price
  tail = 1;
  trial = artedge1;
  lead = head[trial];
  iterations = 0;

  // new iteration
  do {
    iterations++;
    // pricing basic edges
    tailz = tail;
    newpr = 0;
    do {
      ratez = -dual[tail];
      other = (lead >= 0 ? nodes1 : -nodes1);
      do {
        orig = Math.abs(lead);
        rate = dual[orig] + ratez - cost[trial];
        if (room[trial] < 0) rate = -rate;
        if (room[trial] != 0) {
          if (rate > newpr) {
            newarc = trial;
            newpr = rate;
            newtail = tail;
          }
        }
        trial++;
        lead = head[trial];
      } while ((lead ^ other) > 0);
      tail++;
      if (tail == nodes2) {
        tail = 1;
        trial = artedge1;
        lead = head[trial];
      }
      newprz = true;
      if (newpr != 0) {
        newprz = false;
        break;
      }
    } while (tail != tailz);
    if (newprz) {
      for (p=1; p<=edges; p++)
        flowsol[p] = 0;
      // prepare summary
      infeasible = false;
      value = valuez;
      for (p=1; p<=nodes; p++) {
        i = Math.abs(sptpt[p]);
        if ((flow[p] != 0) && (cost[i] == maxint)) infeasible = true;
        value += cost[i] * flow[p];
      }
      for (p=1; p<=lastslackedge; p++)
        if (room[p] < 0) {
        q = -room[p];
        value += cost[p] * q;
        }
      if (infeasible) return 4;
      arccost[0] = value;
      for (p=1; p<=nodes; p++) {
        q = Math.abs(sptpt[p]);
        room[q] = -flow[p];
      }
      solarc = 0;
      tail = 1;
      trial = artedge1;
      lead = head[trial];
      do {
        other = (lead >= 0 ? nodes1 : -nodes1);
        do {
          load = Math.max(0, -room[trial]) + least[trial];
          if (load != 0) {
            orig = Math.abs(lead);
            solarc++;
            arcsol[0][solarc] = orig;
            arcsol[1][solarc] = tail;
            flowsol[solarc] = load;
          }
          trial++;
          lead = head[trial];
        } while ((lead ^ other) > 0);
        tail++;
      } while (tail != nodes1);
      arcsol[0][0] = solarc;
      return 0;
    }

    // ration test
    newlead = Math.abs(head[newarc]);
    part = Math.abs(room[newarc]);
    jpart = 0;

    // cycle search
    ptr[2] = (room[newarc] >= 0 ? tedges1 : -tedges1);
    ptr[1] = -ptr[2];
    rim[1] = newlead;
    rim[2] = newtail;
    distdiff = dist[newlead] - dist[newtail];
    kpart = 1;
    if (distdiff < 0) kpart = 2;
    if (distdiff != 0) {
      right = rim[kpart];
      point = ptr[kpart];
      q = Math.abs(distdiff);
      for (p=1; p<=q; p++) {
        if ((point ^ sptpt[right]) <= 0) {
          // increase flow
          i = Math.abs(sptpt[right]);
          spare = room[i] - flow[right];
          sparez = -right;
        }
        else {
          // decrease flow
          spare = flow[right];
          sparez = right;
        }
        if (part > spare) {
          part = spare;
          jpart = sparez;
          partz = false;
          if (part == 0) {
            partz = true;
            break;
          }
        }
        right = pred[right];
      }
      if (!partz) rim[kpart] = right;
    }
    if (!partz) {
      do {
        if (rim[1] ==  rim[2]) break;
        for (p=1; p<=2; p++) {
          right = rim[p];
          if ((ptr[p] ^ sptpt[right]) <= 0) {
            // increase flow
            i = Math.abs(sptpt[right]);
            spare = room[i] - flow[right];
            sparez = -right;
          }
          else {
            // decrease flow
            spare = flow[right];
            sparez = right;
          }
          if (part > spare) {
            part = spare;
            jpart = sparez;
            kpart = p;
            partz = false;
            if (part == 0) {
              partz = true;
              break;
            }
          }
          rim[p] = pred[right];
        }
      } while (true);
      if (!partz) left = rim[1];
    }
    partz = false;
    if (part != 0) {
      // update flows
      rim[1] = newlead;
      rim[2] = newtail;
      if (jpart != 0)  rim[kpart] = Math.abs(jpart);
      for (p=1; p<=2; p++) {
        right = rim[p];
        point = (ptr[p] >= 0 ? part : -part);
        do {
          if (right == left) break;
          flow[right] -= point * (sptpt[right] >= 0 ? 1 : -1);
          right = pred[right];
        } while (true);
      }
    }
    if (jpart == 0) {
      room[newarc] = -room[newarc];
      continue;
    }
    ipart = Math.abs(jpart);
    if (jpart <= 0) {
      j = Math.abs(sptpt[ipart]);
      // set old edge to upper bound
      room[j] = -room[j];
    }
    load = part;
    if (room[newarc] <= 0) {
      room[newarc] = -room[newarc];
      load = room[newarc] - load;
      newpr = -newpr;
    }
    if (kpart != 2) {
      vertex1 = newlead;
      vertex2 = newtail;
      curedge = -newarc;
      newpr = -newpr;
    }
    else {
      vertex1 = newtail;
      vertex2 = newlead;
      curedge = newarc;
    }

    // update tree
    i = vertex1;
    j = pred[i];
    distlen = dist[vertex2] + 1;
    if (part != 0) {
      point = (ptr[kpart]  >= 0 ? part: -part);
      do {
        // update dual variable
        dual[i] += newpr;
        n = flow[i];
        flow[i] = load;
        track = (sptpt[i] >= 0 ? 1 : -1);
        spointer = Math.abs(sptpt[i]);
        sptpt[i] = curedge;
        olddist = dist[i];
        distdiff = distlen - olddist;
        dist[i] = distlen;
        focal = i;
        do {
          after = succ[focal];
          if (dist[after] <= olddist) break;
          dist[after] += distdiff;
          dual[after] += newpr;
          focal = after;
        } while (true);
        k = j;
        do {
          l = succ[k];
          if (l == i) break;
          k = l;
        } while (true);
        ipartout = false;
        if (i == ipart) {
          ipartout = true;
          break;
        }
        load = n - point * track;
        curedge = -(track >= 0 ? spointer : -spointer);            
        succ[k] = after;
        succ[focal] = j;
        k = i;
        i = j;
        j = pred[j];
        pred[i] = k;
        distlen++;
      } while (true);
    }
    if (!ipartout) {
      do {
        dual[i] += newpr;
        n = flow[i];
        flow[i] = load;
        track = (sptpt[i] >= 0 ? 1 : -1);
        spointer = Math.abs(sptpt[i]);
        sptpt[i] = curedge;
        olddist = dist[i];
        distdiff = distlen - olddist;
        dist[i] = distlen;
        focal = i;
        do {
          after = succ[focal];
          if (dist[after] <= olddist) break;
          dist[after] += distdiff;
          // udpate dual variable
          dual[after] += newpr;
          focal = after;
        } while (true);
        k = j;
        do {
          l = succ[k];
          if (l == i) break;
          k = l;
        } while (true);
        // test for leaving edge
        if (i == ipart) break;
        load = n;
        curedge = -(track >= 0 ? spointer : -spointer);
        succ[k] = after;
        succ[focal] = j;
        k = i;
        i = j;
        j = pred[j];
        pred[i] = k;
        distlen++;
      } while (true);
    }
    ipartout = false;
    succ[k] = after;
    succ[focal] = succ[vertex2];
    succ[vertex2] = vertex1;
    pred[vertex1] = vertex2;
  } while (true);
}


public static void minimalEquivalentGraph(int n, int m,
              int nodei[], int nodej[], boolean link[])
{
  int i,j,k,nodeu,nodev,n1,low,up,edges,index1,index2,high,kedge=0;
  int nextnode[] = new int[n+1];
  int ancestor[] = new int[n+1];
  int descendant[] = new int[n+1];
  int firstedges[] = new int[n+2];
  int pointer[] = new int[m+1];
  int endnode[] = new int[m+1];
  boolean pathexist[] = new boolean[n+1];
  boolean currentarc[] = new boolean[m+1];
  boolean pexist[] = new boolean[1];
  boolean join,skip,hop;

  n1 = n + 1;
  // set up the forward star representation of the graph
  k = 0;
  for (i=1; i<=n; i++) {
    firstedges[i] = k + 1;
    for (j=1; j<=m; j++)
      if (nodei[j] == i) {
        k++;
        pointer[k] = j;
        endnode[k] = nodej[j];
        currentarc[k] = true;
      }
  }
  firstedges[n1] = m + 1;
  // compute number of descendants and ancestors of each node
  for (i=1; i<=n; i++) {
    descendant[i] = 0;
    ancestor[i] = 0;
  }
  edges = 0;
  for (k=1; k<=m; k++) {
    i = nodei[k];
    j = nodej[k];
    descendant[i]++;
    ancestor[j]++;
    edges++;
  }
  if (edges == n) {
    for (k=1; k<=m; k++)
      link[pointer[k]] = currentarc[k];
    return;
  }
  index1 = 0;
  for (k=1; k<=m; k++) {
    i = nodei[pointer[k]];
    j = nodej[pointer[k]];
    // check for the existence of an alternative path
    if (descendant[i] != 1) {
      if (ancestor[j] != 1) {
        currentarc[k] = false;
        minimalEqGraphFindp(n,m,n1,i,j,endnode,firstedges,
                     currentarc,pexist,nextnode,pathexist);
        if (pexist[0]) {
          descendant[i]--;
          ancestor[j]--;
          index1++;
        }
        else
          currentarc[k] = true;
      }
    }
  }
  if (index1 == 0) {
    for (k=1; k<=m; k++)
      link[pointer[k]] = currentarc[k];
    return;
  }
  high = 0;
  nodeu = n;
  nodev = n;
  // store the current best solution
  iterate:
  while (true) {
    for (k=1; k<=m; k++) 
      link[k] = currentarc[k];
    index2 = index1;
    if ((edges - index2) == n) {
      for (k=1; k<=m; k++)
        currentarc[k] = link[k];
      for (k=1; k<=m; k++)
        link[pointer[k]] = currentarc[k];
      return;
    }
    // forward move
    while (true) {
      join = false;
      low = firstedges[nodeu];
      up = firstedges[nodeu + 1];
      if (up > low) {
        up--;
        for (k=low; k<=up; k++)
          if (endnode[k] == nodev) {
            join = true;
            kedge = k;
            break;
          }
      }
      hop = false;
      if (join) {
        if (!currentarc[kedge]) {
          currentarc[kedge] = true;
          descendant[nodeu]++;
          ancestor[nodev]++;
          index1--;
          if (index1 + high - (n - nodeu) > index2) {
            if (nodev != n)
              nodev++;
            else {
              if (nodeu != n) {
                nodeu++;
                nodev = 1;
              }
              else
                continue iterate;
            }
            while (true) {
              // backtrack move
              join = false;
              low = firstedges[nodeu];
              up = firstedges[nodeu + 1];
              if (up > low) {
                 up--;
                 for (k=low; k<=up; k++)
                   if (endnode[k] == nodev) {
                     join = true;
                     kedge = k;
                     break;
                   }
              }
              if (join) {
                high--;
                skip = false;
                if (descendant[nodeu] != 1) {
                  if (ancestor[nodev] != 1) {
                    currentarc[kedge] = false;
                    minimalEqGraphFindp(n,m,n1,nodeu,nodev,endnode,
                      firstedges,currentarc,pexist,nextnode,pathexist);
                    if (pexist[0]) {
                      descendant[nodeu]--;
                      ancestor[nodev]--;
                      index1++;
                      skip = true;
                    }
                    else
                      currentarc[kedge] = true;
                  }
                }
                if (!skip) {
                  if (index1 + high - (n - nodeu) <= index2) {
                    high++;
                    hop = true;
                    break;
                  }
                }
                // check for the termination of the forward move
                if (high - (n - nodeu) == 0) continue iterate;
              }
              if (nodev != n)
                nodev++;
              else {
                if (nodeu != n) {
                  nodeu++;
                  nodev = 1;
                }
                else
                  continue iterate;
              }
            }
          }
        }
        if (!hop) high++;
      }
      hop = false;   
      if (nodev != 1) {
        nodev--;
        continue;
      }
      if (nodeu == 1) {
        for (k=1; k<=m; k++)
          currentarc[k] = link[k];
        for (k=1; k<=m; k++)
          link[pointer[k]] = currentarc[k];
        return;
      }
      nodeu--;
      nodev = n;
    }
  }
}


public static int oneShortestPath(int n, int m, int nodei[], int nodej[],
                         int weight[], int source, int sink, int path[])
{
  int key1,key2,large,distance,index,level1,level2,lensum,temp,mnode=0;
  int i,j,num1,num2,numnodes,pathlength,minlength1,minlength2,minlength3;
  int parent[] = new int[n+1];
  int child[] = new int[n+1];
  int fromsource[] = new int[n+1];
  int tosink[] = new int[n+1];
  int stack1[] = new int[n+1];
  int stack2[] = new int[n+1];
  int dist[][] = new int[n+1][n+1];

  // set up the distance matrix
  large = 1;
  for (i=1; i<=m; i++)
    large += weight[i];
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      dist[i][j] = (i == j) ? 0 : large;
  for (i=1; i<=m; i++)
    dist[nodei[i]][nodej[i]] = weight[i];
  key1 = 0;
  key2 = 0;
  for (i=1; i<=n; i++) {
    fromsource[i] = dist[source][i];
    tosink[i] = dist[i][sink];
    parent[i] = source;
    child[i] = sink;
  }
  // find the initial values of minlength1 and minlength2 with 
  // corresponding 'index' values for 'fromsource' and 'tosink'
  level1 = 0;
  level2 = 0;
  minlength2 = large;
  minlength1 = large;
  for (i=1; i<=n; i++) {
    // finds minlength1 and stores in stack1[1:level1] all values of k
    // such that fromsource[k] = minlength1 and fromsource[i] = key1
    temp = fromsource[i];
    if (temp > key1) {
      if (temp < minlength1) {
        level1 = 1;
        minlength1 = temp;
        stack1[level1] = i;
      }
      else {
        if (temp == minlength1) {
          level1++;
          stack1[level1] = i;
        }
      }
    }
    // finds minlength2 and stores in stack2[1:level2] all values
    // of k such that tosink[k] = minlength2 and tosink[i] = key2
    temp = tosink[i];
    if (temp > key2) {
      if (temp < minlength2) {
        level2 = 1;
        minlength2 = temp;
        stack2[level2] = i;
      }
      else {
        if (temp == minlength2) {
          level2++;
          stack2[level2] = i;
        }
      }
    }
  }
  do {
    if (minlength1 <= minlength2) {
      // reset fromsource
      key1 = minlength1;
      while (true) {
        if (level1 <= 0) break;
        index = stack1[level1];
        for (i=1; i<=n; i++) {
          distance = dist[index][i];
          lensum = minlength1 + distance;
          if (fromsource[i] > lensum) {
            fromsource[i] = lensum;
            parent[i] = index;
          }
        }
        level1--;
      }
      // find new 'minlength1' and 'index' values for 'fromsource'
      minlength1 = large;
      level1 = 0;
      for (i=1; i<=n; i++) {
        // finds minlength2 and stores in stack2[1:level2] all values
        // of k such that tosink[k] = minlength2 and tosink[i] = key2
        temp = fromsource[i];
        if (temp > key1) {
          if (temp < minlength1) {
            level1 = 1;
            minlength1 = temp;
            stack1[level1] = i;
          }
          else {
            if (temp == minlength1) {
              level1++;
              stack1[level1] = i;
            }
          }
        }
      }
    }
    else {
      // reset tosink
      key2 = minlength2;
      while (true) {
        if (level2 <= 0) break;
        index = stack2[level2];
        for (i=1; i<=n; i++) {
          distance = dist[i][index];
          lensum = minlength2 + distance;
          if (tosink[i] > lensum) {
            tosink[i] = lensum;
            child[i] = index;
          }
        }
        level2--;
      }
      // find new 'minlength2' and 'index' values for 'tosink'
      minlength2 = large;
      level2 = 0;
      for (i=1; i<=n; i++) {
        // finds minlength2 and stores in stack2[1:level2] all values
        // of k such that tosink[k] = minlength2 and tosink[i] = key2
        temp = tosink[i];
        if (temp > key2) {
          if (temp < minlength2) {
            level2 = 1;
            minlength2 = temp;
            stack2[level2] = i;
          }
          else {
            if (temp == minlength2) {
              level2++;
              stack2[level2] = i;
            }
          }
        }
      }
    }
    // compute convergence criterion
    minlength3 = large;
    for (i=1; i<=n; i++) {
      lensum = fromsource[i] + tosink[i];
      if (lensum < minlength3) {
        minlength3 = lensum;
        mnode = i;
      }
    }
  } while (minlength3 > minlength1 + minlength2);
  // two ends of a shortest path meet in 'mnode' unravel the path
  num1 = mnode;
  path[n] = mnode;
  if (mnode != source) {
    numnodes = n - 1;
    while (true) {
      num2 = parent[num1];
      if (num2 == source) break;
      num1 = num2;
      path[numnodes] = num2;
      numnodes--;
    }
  }
  else
    numnodes = n;
  path[1] = source;
  num1 = numnodes + 1;
  numnodes = 2;
  while (num1 <= n) {
    path[numnodes] = path[num1];
    numnodes++;
    num1++;
  }
  if (mnode != sink) {
    num1 = mnode;
    while (true) {
      num2 = child[num1];
      if (num2 == sink) break;
        num1 = num2;
        path[numnodes] = num2;
        numnodes++;
    }
    path[numnodes] = sink;
  }
  pathlength = fromsource[mnode] + tosink[mnode];
  path[0] = numnodes;
  return pathlength;
}


public static boolean planarityTesting(int n, int m, int nodei[], int nodej[])
{
  int i, j, k, n1, n2, m2, nm2, n2m, nmp2, m7n5, m22, m33, mtotal; 
  int node1, node2, qnode, tnode, tnum, aux1, aux2, aux3, aux4;

  int level[] = new int[1];
  int initp[] = new int[1];
  int snode[] = new int[1];
  int pnum[] = new int[1];
  int snum[] = new int[1];
  int nexte[] = new int[1];
  int store1[] = new int[1];
  int store2[] = new int[1];
  int store3[] = new int[1];
  int store4[] = new int[1];
  int store5[] = new int[1];
  int mark[] = new int[n + 1];
  int trail[] = new int[n + 1];
  int descendant[] = new int[n + 1];
  int firstlow[] = new int[n + 1];
  int secondlow[] = new int[n + 1];
  int nodebegin[] = new int[n + n + 1];
  int wkpathfind5[] = new int[m + 1];
  int wkpathfind6[] = new int[m + 1];
  int stackarc[] = new int[m + m + 1];
  int stackcolor1[] = new int[m + m + 3];
  int stackcolor2[] = new int[m + m + 3];
  int stackcolor3[] = new int[m + m + 3];
  int stackcolor4[] = new int[m + m + 3];
  int wkpathfind1[] = new int[m + m + 3];
  int wkpathfind2[] = new int[m + m + 3];
  int wkpathfind3[] = new int[m + m + m + 4];
  int wkpathfind4[] = new int[m + m + m + 4];
  int first[] = new int[n + m + m + 1];
  int second[] = new int[n + m + m + 1];
  int sortn[] = new int[n + n + m + 1];
  int sortptr1[] = new int[n + n + m + 1];
  int sortptr2[] = new int[n + n + m + 1];
  int start[] = new int[m - n + 3];
  int finish[] = new int[m - n + 3];
  int paint[] = new int[m - n + 3];
  int nextarc[] = new int[7*m - 5*n + 3];
  int arctop[] = new int[7*m - 5*n + 3];
  boolean middle[] = new boolean[1];
  boolean fail[] = new boolean[1];
  boolean examin[] = new boolean[m - n + 3];
  boolean arctype[] = new boolean[7*m - 5*n + 3];

  // check for the necessary condition
  if (m > 3*n-6)
    return false;
  n2 = n + n;
  m2 = m + m;
  nm2 = n + m + m;
  n2m = n + n + m;
  m22 = m + m + 2;
  m33 = m + m + m + 3;
  nmp2 = m - n + 2;
  m7n5 = 7 * m - 5 * n + 2;
  // set up graph representation
  for (i=1; i<=n; i++)
    second[i] = 0;
  mtotal = n;
  for (i=1; i<=m; i++) {
    node1 = nodei[i];
    node2 = nodej[i];
    mtotal++;
    second[mtotal] = second[node1];
    second[node1] = mtotal;
    first[mtotal] = node2;
    mtotal++;
    second[mtotal] = second[node2];
    second[node2] = mtotal;
    first[mtotal] = node1;
  }
  // initial depth-first search, compute low point functions
  for (i=1; i<=n; i++) {
    mark[i] = 0;
    firstlow[i] = n + 1;
    secondlow[i] = n + 1;
  }
  snum[0] = 1;
  store1[0]  = 0;
  mark[1] = 1;
  wkpathfind5[1] = 1;
  wkpathfind6[1] = 0;
  level[0]  = 1;
  middle[0] = false;
  do {
    planarityDFS1(n,m,m2,nm2,level,middle,snum,store1,mark,firstlow,
                  secondlow,wkpathfind5,wkpathfind6,stackarc,first,second);
  } while (level[0] > 1);
  for (i=1; i<=n; i++)
    if (secondlow[i] >= mark[i])  secondlow[i] = firstlow[i];
  // radix sort
  mtotal = n2;
  k = n2;
  for (i=1; i<=n2; i++)
    sortn[i] = 0;
  for (i=2; i<=m2; i+=2) {
    k++;
    sortptr1[k] = stackarc[i-1];
    tnode = stackarc[i];
    sortptr2[k] = tnode;
    if (mark[tnode] < mark[sortptr1[k]]) {
      j = 2 * mark[tnode] - 1;
      sortn[k] = sortn[j];
      sortn[j] = k;
    }
    else {
      if (secondlow[tnode] >= mark[sortptr1[k]]) {
        j = 2 * firstlow[tnode] - 1;
        sortn[k] = sortn[j];
        sortn[j] = k;
      }
      else {
        j = 2 * firstlow[tnode];
        sortn[k] = sortn[j];
        sortn[j] = k;
      }
    }
  }
  for (i=1; i<=n2; i++) {
    j = sortn[i];
    while (j != 0) {
      node1 = sortptr1[j];
      node2 = sortptr2[j];
      mtotal++;
      second[mtotal] = second[node1];
      second[node1] = mtotal;
      first[mtotal] = node2;
      j = sortn[j];
    }
  }
  // second depth-first search
  for (i=2; i<=n; i++)
    mark[i] = 0;
  store1[0] = 0;
  snum[0] = 1;
  trail[1] = 1;
  wkpathfind5[1] = 1;
  start[1] = 0;
  finish[1] = 0;
  level[0] = 1;
  middle[0] = false;
  do {
    planarityDFS2(n,m,m2,nm2,level,middle,snum,store1,mark,
                  wkpathfind5,stackarc,first,second);
  } while (level[0] > 1);
  mtotal = n;
  for (i=1; i<=m; i++) {
    j = i + i;
    node1 = stackarc[j-1];
    node2 = stackarc[j];
    mtotal++;
    second[mtotal] = second[node1];
    second[node1] = mtotal;
    first[mtotal] = node2;
  }
  // path decomposition, construction of hte dependency graph
  store2[0]  = 0;
  store3[0]  = 0;
  store4[0]  = 0;
  store5[0]  = 0;
  initp[0] = 0;
  pnum[0]  = 1;
  wkpathfind1[1] = 0;
  wkpathfind1[2] = 0;
  wkpathfind2[1] = 0;
  wkpathfind2[2] = 0;
  wkpathfind3[1] = 0;
  wkpathfind3[2] = n + 1;
  wkpathfind3[3] = 0;
  wkpathfind4[1] = 0;
  wkpathfind4[2] = n + 1;
  wkpathfind4[3] = 0;
  for (i=1; i<=n2; i++)
    nodebegin[i] = 0;
  nexte[0] = m - n + 1;
  for (i=1; i<=m7n5; i++)
    nextarc[i] = 0;
  snode[0] = n;
  descendant[1] = n;
  wkpathfind5[1] = 1;
  level[0]  = 1;
  middle[0] = false;
  do {
    planarityDecompose(n,m,n2,m22,m33,nm2,nmp2,m7n5,level,middle,initp,
           snode,pnum,nexte,store2,store3,store4,store5,trail,descendant,
           nodebegin,wkpathfind5,start,finish,first,second,wkpathfind1,
           wkpathfind2,wkpathfind3,wkpathfind4,nextarc,arctop,arctype);
  } while (level[0] > 1);
  // perform two-coloring
  pnum[0]--;
  for (i=1; i<=nmp2; i++)
    paint[i] = 0;
  j = pnum[0] + 1;
  for (i=2; i<=j; i++)
    examin[i] = true;
  tnum = 1;
  while (tnum <= pnum[0]) {
    wkpathfind5[1] = tnum;
    paint[tnum] = 1;
    examin[tnum] = false;
    level[0] = 1;
    middle[0] = false;
    do {
      planarityTwoColoring(m,nmp2,m7n5,level,middle,fail,wkpathfind5,
                           paint,nextarc,arctop,examin,arctype);
      if (fail[0])
        return false;
    } while (level[0] > 1);
    while (!examin[tnum])
      tnum++;
  }
  aux1 = 0;
  aux2 = 0;
  aux3 = 0;
  aux4 = 0;
  stackcolor1[1] = 0;
  stackcolor1[2] = 0;
  stackcolor2[1] = 0;
  stackcolor2[2] = 0;
  stackcolor3[1] = 0;
  stackcolor3[2] = 0;
  stackcolor4[1] = 0;
  stackcolor4[2] = 0;
  for (i=1; i<=pnum[0]; i++) {
    qnode = start[i+1];
    tnode = finish[i+1];
    while (qnode <= stackcolor1[aux1+2])
      aux1 -= 2;
    while (qnode <= stackcolor2[aux2+2])
      aux2 -= 2;
    while (qnode <= stackcolor3[aux3+2])
      aux3 -= 2;
    while (qnode <= stackcolor4[aux4+2])
      aux4 -= 2;
    if (paint[i] == 1) {
      if (finish[trail[qnode]+1] != tnode) {
        if (tnode < stackcolor2[aux2+2])
          return false;
        if (tnode < stackcolor3[aux3+2])
          return false;
        aux3 += 2;
        stackcolor3[aux3 + 1] = i;
        stackcolor3[aux3 + 2] = tnode;
      }
      else {
        if ((tnode < stackcolor3[aux3+2]) &&
            (start[stackcolor3[aux3+1]+1] <= descendant[qnode]))
          return false;
        aux1 += 2;
        stackcolor1[aux1 + 1] = i;
        stackcolor1[aux1 + 2] = qnode;
      }
    }
    else {
      if (finish[trail[qnode]+1] != tnode) {
        if (tnode < stackcolor1[aux1+2])
          return false;
        if (tnode < stackcolor4[aux4+2])
          return false;
        aux4 += 2;
        stackcolor4[aux4 + 1] = i;
        stackcolor4[aux4 + 2] = tnode;
      }
      else {
        if ((tnode < stackcolor4[aux4+2]) &&
            (start[stackcolor4[aux4+1]+1] <= descendant[qnode]))
          return false;
        aux2 += 2;
        stackcolor2[aux2 + 1] = i;
        stackcolor2[aux2 + 2] = qnode;
      }
    }
  }
  return true;
}


public static void quadraticAssignment(int n, int a[][], int b[][], int sol[])
{
  int valc,vald,valf,valg,valh,vali,valk,valp,valq,valw,valx,valy;
  int i,j,k,p,rowdist,rowweight,rowmata,rowmatb,partialobj;
  int leastw,leastx,leasty,matbx,matby,matcx,matcy,matax;
  int bestobjval,ntwo,npp,large;
  int vale=0,subcripta=0,subcriptb=0,subcriptc=0,subcriptd=0,partialz=0;
  int parm1[] = new int[1];
  int parm2[] = new int[1];
  int parm3[] = new int[1];
  int aux2[] = new int[n-1];
  int aux3[] = new int[n-1];
  int aux4[] = new int[n-1];
  int aux5[] = new int[n-1];
  int aux6[] = new int[n-1];
  int aux7[] = new int[n+1];
  int aux8[] = new int[n+1];
  int aux9[] = new int[n+1];
  int aux10[] = new int[n+1];
  int aux11[] = new int[n+1];
  int aux12[] = new int[n+1];
  int aux13[] = new int[n+1];
  int aux14[] = new int[n+1];
  int aux15[] = new int[n*n+1];
  int aux16[] = new int[n*(n+1)*(2*n-2)/6 + 1];
  int aux17[] = new int[n*(n+1)*(2*n-2)/6 + 1];
  int aux18[] = new int[(n*(n+1)*(2*n+1)/6)];
  int aux19[][] = new int[n+1][n+1];
  int aux20[][] = new int[n+1][n+1];
  boolean work1[] = new boolean[n+1];
  boolean work2[] = new boolean[n+1];
  boolean work3[] = new boolean[n+1];
  boolean contr,skip=false;

  // initialization
  p = 1;
  k = 1;
  for (i=1; i<=n; i++) {
    for (j=1; j<=n; j++) {
      p += a[i][j];
      k += b[i][j];
      aux19[i][j] = 0;
    }
    aux13[i] = 0;
  }
  large = n * p * k;
  k = 0;
  bestobjval = large;
  ntwo = n - 2;
  valy = n + 1;
  j = 0;
  valp = 0;
  for (i=1; i<=ntwo; i++) {
    npp = valy - i;
    valq = npp * npp;
    j += valq - npp;
    aux4[i] = j;
    valp += valq;
    aux5[i] = valp;
  }
  // compute a[i][i] * b[j][j]
  for (i=1; i<=n; i++) {
    work1[i] = false;
    work2[i] = false;
    partialz = a[i][i];
    for (j=1; j<=n; j++) {
      aux20[i][j] = -1;
      aux19[i][j] += partialz * b[j][j];
    }
  }
  for (j=1; j<=n; j++) {
    a[j][j] = large;
    b[j][j] = large;
  }
  // reduce matrices a and b
  for (i=1; i<=n; i++) {
    rowmata = a[i][1];
    rowmatb = b[i][1];
    for (j=2; j<=n; j++) {
      rowdist = a[i][j];
      rowweight = b[i][j];
      if (rowdist < rowmata) rowmata = rowdist;
      if (rowweight < rowmatb) rowmatb = rowweight;
    }
    for (j=1; j<=n; j++) {
      a[i][j] -= rowmata;
      b[i][j] -= rowmatb;
    }
    aux7[i] = rowmata;
    aux8[i] = rowmatb;
  }
  // rowwise reduction of matrices a and b
  for (i=1; i<=n; i++) {
    matcy = aux7[i];
    for (j=1; j<=n; j++) {
      matbx = aux8[j];
      matby = (n-1) * matbx;
      for (p=1; p<=n; p++)
        if (p != j) matby += b[j][p];
      matcx = matcy * matby;
      matax = 0;
      for (p=1; p<=n; p++)
        if (p != i) matax += a[i][p];
      aux19[i][j] += matcx + matbx * matax;
    }
  }
  // columnwise reduction of matrices a and b
  for (i=1; i<=n; i++) {
    rowmata = a[1][i];
    rowmatb = b[1][i];
    for (j=2; j<=n; j++) {
      rowdist = a[j][i];
      rowweight = b[j][i];
      if (rowdist < rowmata) rowmata = rowdist;
      if (rowweight < rowmatb) rowmatb = rowweight;
    }
     for (j=1; j<=n; j++) {
       a[j][i] -= rowmata;
       b[j][i] -= rowmatb;
    }
    aux7[i] = rowmata;
    aux8[i] = rowmatb;
  }
  for (i=1; i<=n; i++) {
    a[i][i] = 0;
    b[i][i] = 0;
    matcy = aux7[i];
    for (j=1; j<=n; j++) {
      matbx = aux8[j];
      matby = (n-1) * matbx;
      for (p=1; p<=n; p++)
        if (p != j) matby += b[p][j];
      matcx = matcy * matby;
      matax = 0;
      for (p=1; p<=n; p++)
        if (p != i) matax += a[p][i];
      aux19[i][j] += matcx + matbx * matax;
    }
  }
  partialobj = 0;
  npp = n;
  contr = true;
  // compute minimal scalar products
  qapsubprog2(n,k,npp,subcripta,subcriptb,a,b,aux4,aux7,
              work1,work2,aux16,aux17,aux13);
  qapsubprog3(n,k,npp,aux15,aux4,aux5,contr,aux16,aux17,aux18);
  contr = false;
  iterate:
  while (true) {
    valx = 0;
    valy = 0;
    valq = 0;
    valf = 0;
    for (i=1; i<=n; i++)
      if (!work1[i]) {
        valc = valx * npp;
        valx++;
        vald = 0;
        for (j=1; j<=n; j++)
          if (!work2[j]) {
            vald++;
            valc++;
            if (aux20[i][j] < 0)
              aux15[valc] += aux19[i][j];
            else {
              aux15[valc] = large;
              valf++;
              if (valf < 2) {
                subcriptc = valx;
                subcriptd = vald;
              }
              else {
                if (valf == 2) {
                  if (valx == subcriptc) valy = valx;
                  if (vald == subcriptd) valq = vald;
                }
              }
            }
          }
      }
    // obtain a bound by solving the linear assignment problem
    skip = false;
    obtainbound:
    while (true) {
      if (!skip) {
        qapsubprog4(npp,large,aux15,parm1,aux14,aux12,aux11,
                    aux9,aux7,aux8,aux13,work3);
        partialz = parm1[0];
        valc = 0;
        for (i=1; i<=npp; i++)
          for (j=1; j<=npp; j++) {
            valc++;
            aux15[valc] -= (aux7[i] + aux8[j]);
          }
        if (partialobj + partialz >= bestobjval) {
          // backtrack
          if (!contr) {
            if (k == 0) {
              return;
            }
            subcripta = aux2[k];
            subcriptb = aux3[k];
          }
          else {
            contr = false;
            k++;
            // cancel the last single assignment
            for (i=1; i<=n; i++)
              if (!work1[i]) {
                for (j=1; j<=n; j++)
                  if (!work2[j] && aux20[i][j] == k) aux20[i][j] = -1;
              }
            partialobj -= aux19[subcripta][subcriptb];
            work1[subcripta] = false;
            work2[subcriptb] = false;
            k--;
            npp = n - k;
            if (aux6[k+1] + partialobj >= bestobjval) {
              if (k == 0) {
                return;
              }
              subcripta = aux2[k];
              subcriptb = aux3[k];
            }
            else {
              qapsubprog3(n,k,npp,aux15,aux4,aux5,contr,aux16,aux17,aux18);
              continue iterate;
            }
          }
          skip = true;
          continue obtainbound;
        }
        if (contr) {
          skip = true;
          continue obtainbound;
        }
        skip = false;
      }
      if (skip) {
        skip = false;
        // the solution tree is completed
        for (i=1; i<=n; i++)
          if (!work1[i]) {
            valh = a[subcripta][i];
            vali = a[i][subcripta];
            for (j=1; j<=n; j++)
              if (!work2[j]) {
                valg = valh * b[subcriptb][j] + vali * b[j][subcriptb];
                if (!contr) valg = -valg;
                aux19[i][j] += valg;
              }
          }
        if (!contr) {
          // cancel the last single assignment
          for (i=1; i<=n; i++)
            if (!work1[i]) {
              for (j=1; j<=n; j++)
                if (!work2[j] && aux20[i][j] == k) aux20[i][j] = -1;
            }
          partialobj -= aux19[subcripta][subcriptb];
          work1[subcripta] = false;
          work2[subcriptb] = false;
          k--;
          npp = n - k;
          if (aux6[k+1] + partialobj >= bestobjval) {
            if (k == 0) {
              return;
            }
            subcripta = aux2[k];
            subcriptb = aux3[k];
            skip = true;
            continue obtainbound;
          }
          qapsubprog3(n,k,npp,aux15,aux4,aux5,contr,aux16,aux17,aux18);
          continue iterate;
        }
        aux10[subcripta] = subcriptb;
        k++;
        aux2[k] = subcripta;
        aux3[k] = subcriptb;
        if (k == (n-2)) {
          // compute the objective function values
          for (i=1; i<=n; i++)
            if (!work1[i]) {
              valx = i;
              break;
            }
          for (i=1; i<=n; i++)
            if (!work2[i]) {
              j = i;
              break;
            }
          work1[valx] = true;
          work2[j] = true;
          for (i=1; i<=n; i++)
            if (!work1[i]) {
              vale = i;
              break;
            }
          for (i=1; i<=n; i++)
            if (!work2[i]) {
              valp = i;
              break;
            }
          contr = false;
          valw = 0;
          while (true) {
            partialz = aux19[valx][j] + aux19[vale][valp] + 
               a[valx][vale] * b[j][valp] + a[vale][valx] * b[valp][j];
            work1[valx] = false;
            work2[j] = false;
            if ((partialz + partialobj) < bestobjval) {
              bestobjval = partialz + partialobj;
              sol[0] = bestobjval;
              for (i=1; i<=n; i++)
                if (work1[i]) sol[i] = aux10[i];
              sol[valx] = j;
              sol[vale] = valp;
            }
            if (valw != 0) {
              if (k == 0) {
                return;
              }
              subcripta = aux2[k];
              subcriptb = aux3[k];
              skip = true;
              continue obtainbound;
            }
            valw = valx;
            valx = vale;
            vale = valw;
          }
        }
        valf = 0;
      }
      if (valf < 1) {
        // compute the alternative costs
        qapsubprog1(npp,aux15,aux12,large,parm1,parm2,parm3);
        subcripta = parm1[0];
        subcriptb = parm2[0];
        valk = parm3[0];
      }
      else {
        if (valf == 1) {
          // compute the next single assignment
          leastw = large;
          valp = aux12[subcriptc];
          valc = (subcriptc - 1) * npp;
          for (j=1; j<=npp; j++) {
            valc++;
            valg = aux15[valc];
            if ((leastw > valg) && (j != valp)) leastw = valg;
          }
          leastx = leastw;
          leastw = large;
          valc = valp;
          for (i=1; i<=npp; i++) {
            valg = aux15[valc];
            valc += npp;
            if ((leastw > valg) && (i != subcriptc)) leastw = valg;
          }
          leastx += leastw;
          leastw = large;
          valc = subcriptd;
          for (i=1; i<=npp; i++) {
            valg = aux15[valc];
            valc += npp;
            if ((valg < leastw) && (subcriptd != aux12[i])) leastw = valg;
          }
          leasty = leastw;
          i = 1;
          while (i <= npp) {
            if (aux12[i] == subcriptd) break;
            i++;
          }
          vale = i;
          valc = (vale - 1) * npp;
          leastw = large;
          for (j=1; j<=npp; j++) {
            valc++;
            valg = aux15[valc];
            if ((valg < leastw) && (j != subcriptd)) leastw = valg;
          }
          if ((leastw + leasty) >= leastx) {
            subcripta = vale;
            subcriptb = subcriptd;
            valk = leastw + leasty;
          }
          else {
            subcripta = subcriptc;
            subcriptb = valp;
            valk = leastx;
          }
        }
        else {
          // compute the next single assignment
          if (valy != 0) {
            subcripta = valy;
            subcriptb = aux12[subcripta];
          }
          else {
            subcriptb = valq;
            i = 1;
            while (i <= npp) {
              if (aux12[i] == subcriptb) break;
              i++;
            }
            subcripta = i;
          }
          leastw = large;
          valc = (subcripta - 1) * npp;
          for (i=1; i<=npp; i++) {
            valc++;
            valg = aux15[valc];
            if ((valg < leastw) && (i != subcriptb)) leastw = valg;
          }
          valk = leastw;
          leastw = large;
          valc = subcriptb;
          for (j=1; j<=npp; j++) {
            valg = aux15[valc];
            valc += npp;
            if ((valg < leastw) && (j != subcripta)) leastw = valg;
          }
          valk += leastw;
        }
      }
      valx = 0;
      aux6[k+1] = valk + partialz;
      i = 1;
      while (i <= n) {
        if (!work1[i]) {
          valx++;
          if (subcripta == valx) break;
        }
        i++;
      }
      subcripta = i;
      valx = 0;
      j = 1;
      while (j <= n) {
        if (!work2[j]) {
          valx++;
          if (subcriptb == valx) break;
        }
        j++;
      }
      subcriptb = j;
      aux20[subcripta][subcriptb] = k;
      contr = true;
      work1[subcripta] = true;
      work2[subcriptb] = true;
      npp = n - k - 1;
      // compute the cost matrix
      qapsubprog2(n,k,npp,subcripta,subcriptb,a,b,aux4,aux7,
                  work1,work2,aux16,aux17,aux13);
      qapsubprog3(n,k,npp,aux15,aux4,aux5,contr,aux16,aux17,aux18);
      valx = 0;
      for (i=1; i<=n; i++)
        if (!work1[i]) {
          valh = a[i][subcripta];
          vali = a[subcripta][i];
          vald = valx;
          for (j=1; j<=n; j++)
            if (!work2[j]) {
              vald++;
              aux15[vald] += aux19[i][j] + valh * b[j][subcriptb] + 
                                           vali * b[subcriptb][j];
            }
          valx += npp;
        }
      partialobj += aux19[subcripta][subcriptb];
    }
  }
}


public static void randomBipartiteGraph(int n1, int n2, int m, 
                          long seed, int nodei[], int nodej[])
{
  int n,nodea,nodeb,nodec,numedges;
  boolean adj[][] = new boolean[n1+n2+1][n1+n2+1];
  boolean temp;
  Random ran = new Random(seed);

  n = n1 + n2;
  // initialize the adjacency matrix
  for (nodea=1; nodea<=n; nodea++)
    for (nodeb=1; nodeb<=n; nodeb++)
      adj[nodea][nodeb] = false;

  if (m != 0) {
    if (m > n1 * n2) m = n1 * n2;
    numedges = 0;
    // generate a simple bipartite graph with exactly m edges
    while (numedges < m) {
      // generate a random integer in interval [1, n1]
      nodea = (int)(1 + ran.nextDouble() * n1);
      // generate a random integer in interval [n1+1, n]
      nodeb = (int)(n1 + 1 + ran.nextDouble() * n2);
      if (!adj[nodea][nodeb]) {
        // add the edge (nodei,nodej)
        adj[nodea][nodeb] = adj[nodeb][nodea] = true;
        numedges++;
      }
    }
  }
  else {
    // generate a random adjacency matrix with edges from
    // nodes of group [1, n1] to nodes of group [n1+1, n]
    for (nodea=1; nodea<=n1; nodea++)
      for (nodeb=n1+1; nodeb<=n; nodeb++)
        adj[nodea][nodeb] = adj[nodeb][nodea] =
                    (ran.nextInt(2) == 0) ? false : true;
  }
  // random permutation of rows and columns
  for (nodea=1; nodea<=n; nodea++) {
    nodec = (int)(nodea + ran.nextDouble() * (n + 1 - nodea));
    for (nodeb=1; nodeb<=n; nodeb++) {
      temp = adj[nodec][nodeb];
      adj[nodec][nodeb] = adj[nodea][nodeb];
      adj[nodea][nodeb] = temp;
    }
    for (nodeb=1; nodeb<=n; nodeb++) {
      temp = adj[nodeb][nodec];
      adj[nodeb][nodec] = adj[nodeb][nodea];
      adj[nodeb][nodea] = temp;
    }
  }
  numedges = 0;
  for (nodea=1; nodea<=n; nodea++)
    for (nodeb=nodea+1; nodeb<=n; nodeb++)
      if (adj[nodea][nodeb]) {
        numedges++;
        nodei[numedges] = nodea;
        nodej[numedges] = nodeb;
      }
  nodei[0] = numedges;
}


public static int randomConnectedGraph(int n, int m, long seed,
       boolean weighted, int minweight, int maxweight, 
       int nodei[], int nodej[], int weight[])
{
  int maxedges,nodea,nodeb,numedges,temp;
  int permute[] = new int[n + 1];
  boolean adj[][] = new boolean[n+1][n+1];
  Random ran = new Random(seed);

  // initialize the adjacency matrix
  for (nodea=1; nodea<=n; nodea++)
    for (nodeb=1; nodeb<=n; nodeb++)
      adj[nodea][nodeb] = false;
  numedges = 0;
  // check for valid input data
  if (m < (n - 1)) return 1;
  maxedges = (n * (n - 1)) / 2;
  if (m > maxedges) return 2;
   
  // generate a random spanning tree by the greedy method
  randomPermutation(n,ran,permute);
  for (nodea=2; nodea<=n; nodea++) {
    nodeb = ran.nextInt(nodea - 1) + 1;
    numedges++;
    nodei[numedges] = permute[nodea];
    nodej[numedges] = permute[nodeb];
    adj[permute[nodea]][permute[nodeb]] = true;
    adj[permute[nodeb]][permute[nodea]] = true;
    if (weighted)
      weight[numedges] = (int)(minweight + 
            ran.nextDouble() * (maxweight + 1 - minweight));
  }
  // add the remaining edges randomly
  while (numedges < m) {
    nodea = ran.nextInt(n) + 1;
    nodeb = ran.nextInt(n) + 1;
  	if (nodea == nodeb) continue;
    if (nodea > nodeb) {
  	  temp = nodea;
      nodea = nodeb;
      nodeb = temp;
    }
  	if (!adj[nodea][nodeb]) {
      numedges++;
      nodei[numedges] = nodea;
      nodej[numedges] = nodeb;
      adj[nodea][nodeb] = true;
      if (weighted)
        weight[numedges] = (int)(minweight + 
              ran.nextDouble() * (maxweight + 1 - minweight));
    }
  }
  return 0;
}


public static int randomGraph(int n, int m, long seed,
       boolean simple, boolean directed, boolean acyclic,
       boolean weighted, int minweight, int maxweight, 
       int nodei[], int nodej[], int weight[])
{
  int maxedges,nodea,nodeb,numedges,temp;
  int dagpermute[] = new int[n + 1];
  boolean adj[][] = new boolean[n+1][n+1];
  Random ran = new Random(seed);

  // initialize the adjacency matrix
  for (nodea=1; nodea<=n; nodea++)
    for (nodeb=1; nodeb<=n; nodeb++)
      adj[nodea][nodeb] = false;
  numedges = 0;
  // check for valid input data
  if (simple) {
    maxedges = n * (n - 1);
    if (!directed) maxedges /= 2;
    if (m > maxedges) return 1;
  }
  if (acyclic) {
    maxedges = (n * (n - 1)) / 2;
    if (m > maxedges) return 1;
    randomPermutation(n,ran,dagpermute);
  }
  while (numedges < m) {
    nodea = ran.nextInt(n) + 1;
    nodeb = ran.nextInt(n) + 1;
    if (simple || acyclic)
  	  if (nodea == nodeb) continue;
    if ((simple && (!directed)) || acyclic)
      if (nodea > nodeb) {
  	    temp = nodea;
        nodea = nodeb;
        nodeb = temp;
      }
  	if (acyclic) {
      nodea = dagpermute[nodea];
      nodeb = dagpermute[nodeb];
  	}
  	if ((!simple) ||
         (simple && (!adj[nodea][nodeb]))) {
      numedges++;
      nodei[numedges] = nodea;
      nodej[numedges] = nodeb;
      adj[nodea][nodeb] = true;
      if (weighted)
        weight[numedges] = (int)(minweight + 
              ran.nextDouble() * (maxweight + 1 - minweight));
    }
  }
  return 0;
}


public static int randomHamiltonGraph(int n, int m, long seed,
       boolean directed, boolean weighted, int minweight, 
       int maxweight, int nodei[], int nodej[], int weight[])
{
  int k,maxedges,nodea,nodeb,numedges,temp;
  int permute[] = new int[n + 1];
  boolean adj[][] = new boolean[n+1][n+1];
  Random ran = new Random(seed);

  // initialize the adjacency matrix
  for (nodea=1; nodea<=n; nodea++)
    for (nodeb=1; nodeb<=n; nodeb++)
      adj[nodea][nodeb] = false;
  // adjust value of m if needed
  if (m < n) return 1;
  maxedges = n * (n - 1);
  if (!directed) maxedges /= 2;
  if (m > maxedges) return 2;
  numedges = 0;
  // generate a random permutation
  randomPermutation(n,ran,permute);
  // obtain the initial cycle
  for (k=1; k<=n; k++) {
  	if (k == n) {
      nodea = permute[n];
      nodeb = permute[1];
  	}
  	else {
      nodea = permute[k];
      nodeb = permute[k + 1];
    }
    numedges++;
    nodei[numedges] = nodea;
    nodej[numedges] = nodeb;
    adj[nodea][nodeb] = true;
    if (!directed) adj[nodeb][nodea] = true;
    if (weighted)
      weight[numedges] = (int)(minweight + 
            ran.nextDouble() * (maxweight + 1 - minweight));
  }
  // add the remaining edges randomly
  while (numedges < m) {
    nodea = ran.nextInt(n) + 1;
    nodeb = ran.nextInt(n) + 1;
  	if (nodea == nodeb) continue;
    if ((nodea > nodeb) && (!directed)) {
  	  temp = nodea;
      nodea = nodeb;
      nodeb = temp;
    }
  	if (!adj[nodea][nodeb]) {
      numedges++;
      nodei[numedges] = nodea;
      nodej[numedges] = nodeb;
      adj[nodea][nodeb] = true;
      if (weighted)
        weight[numedges] = (int)(minweight + 
              ran.nextDouble() * (maxweight + 1 - minweight));
    }
  }
  return 0;
}


public static int randomIsomorphicGraphs(int n, int m, long seed,
       boolean simple, boolean directed, int firsti[], int firstj[],
       int secondi[], int secondj[], int map[])
{
  int k;
  Random ran = new Random(seed);
  
  // generate a random graph
  k = randomGraph(n,m,seed,simple,directed,false,false,0,0,
                  firsti,firstj,map);
  if (k != 0) return k;
  // generate a random permutation
  randomPermutation(n,ran,map);
  // rename the vertices to obtain the isomorphic graph
  for (int i=1; i<=m; i++) {
    secondi[i] = map[firsti[i]];
    secondj[i] = map[firstj[i]];
  }
  return k;
}


public static int randomIsomorphicRegularGraphs(int n, int degree,
       long seed, int firsti[], int firstj[], int secondi[],
       int secondj[], int map[])
{
  int k;
  Random ran = new Random(seed);
    
  // generate a random regular graph
  k = randomRegularGraph(n,degree,seed,firsti,firstj);
  if (k != 0) return k;
  // generate a random permutation
  randomPermutation(n,ran,map);
  // rename the vertices to obtain the isomorphic graph
  for (int i=1; i<=(n*degree)/2; i++) {
    secondi[i] = map[firsti[i]];
    secondj[i] = map[firstj[i]];
  }
  return k;
}


public static void randomLabeledTree(int n, long seed, int sol[])
{
  int i,idxa,idxb,idxc,idxd,idxe,nminus2;
  int neighbor[] = new int[n + 1];
  int prufercode[] = new int[n];
  Random ran = new Random(seed);

  if (n <= 2) return;
  // select n-2 integers at random in [1,n]
  for (i=1; i<=n-2; i++)
    prufercode[i] = (int)(1 + ran.nextDouble() * n);
  // compute the tree from the Pruefer code
  for (i=1; i<=n; i++)
    sol[i] = 0;
  nminus2 = n - 2;
  for (i=1; i<=nminus2; i++) {
    idxc = prufercode[n-1-i];
    if (sol[idxc] == 0) prufercode[n-1-i] = -idxc;
    sol[idxc] = -1;
  }
  idxb = 1;
  prufercode[n-1] = n;
  idxa = 0;
  while (true) {
    if (sol[idxb] != 0) {
	idxb++;
	continue;
    }
    idxd = idxb;
    while (true) {
      idxa++;
      idxe = Math.abs(prufercode[idxa]);
      sol[idxd] = idxe;
	if (idxa == n-1) {
        for (i=1; i<=nminus2; i++) 
          prufercode[i] = Math.abs(prufercode[i]);
        return;
      }
      if (prufercode[idxa] > 0) break;
      if (idxe > idxb) {
        sol[idxe] = 0;
        break;
      }
	idxd = idxe;
    }
  }
}


public static void randomMaximumFlowNetwork(int n, int m, long seed,
                          int minweight, int maxweight, int nodei[], 
                          int nodej[], int weight[])
{
  int i,maxedges,nodea,nodeb,numedges,source,sink;
  boolean adj[][] = new boolean[n+1][n+1];
  boolean marked[] = new boolean[n+1];
  boolean more;
  Random ran = new Random(seed);

  if ((n <= 1) || (m < 1)) return;
  // initialize the adjacency matrix
  for (nodea=1; nodea<=n; nodea++)
    for (nodeb=1; nodeb<=n; nodeb++)
      adj[nodea][nodeb] = false;
  // check for valid input data
  maxedges = n * n - 3 * n + 3;
  if (m > maxedges) m = maxedges;
  nodei[0] = m;
  
  // node 1 is the source and node n is the sink
  source = 1;
  sink = n;
  numedges = 0;
  // initially every node is not on some path from source to sink
  for (i=1; i<=n; i++)
    marked[i] = false;
  // include each node on some path from source to sink */
  marked[source] = true;
  do {
    // choose an edge from source to some node not yet included
    do {
      // generate a random integer in interval [2,n]
      nodeb = (int)(2 + ran.nextDouble() * (n-1));
    } while (marked[nodeb]);
    marked[nodeb] = true;
    // add the edge from source to nodeb
    adj[1][nodeb] = true;
    numedges++;
    weight[numedges] = (int)(minweight + 
          ran.nextDouble() * (maxweight + 1 - minweight));
    nodei[numedges] = 1;
    nodej[numedges] = nodeb;
    if (numedges == m) return;
    // add an edge from current node to a node other than the sink
    if (nodeb != sink) {
      nodea = nodeb;
      marked[sink] = false;
      while (true) {
        do {
          // generate a random integer in interval [2,n]
          nodeb = (int)(2 + ran.nextDouble() * (n-1));
        } while (marked[nodeb]);
        marked[nodeb] = true;
        if (nodeb == sink) break;
        // add an edge from nodea to nodeb
        adj[nodea][nodeb] = true;
        numedges++;
        weight[numedges] = (int)(minweight + 
              ran.nextDouble() * (maxweight + 1 - minweight));
        nodei[numedges] = nodea;
        nodej[numedges] = nodeb;
        if (numedges == m) return;
        nodea = nodeb;
      }
      // add an edge from nodea to sink
      adj[nodea][sink] = true;
      numedges++;
      weight[numedges] = (int)(minweight + 
            ran.nextDouble() * (maxweight + 1 - minweight));
      nodei[numedges] = nodea;
      nodej[numedges] = sink;
      if (numedges == m) return;
    }
    more = false;
    for (i=1; i<n; i++)
      if (!marked[i]) {
        more = true;
        break;
      }
  } while (more);
  // add additional edges if needed
  while (numedges < m) {
    // generate a random integer in interval [1,n-1]
    nodea = (int)(1 + ran.nextDouble() * (n-1));
    // generate a random integer in interval [2,n]
    nodeb = (int)(2 + ran.nextDouble() * (n-1));
    if (!adj[nodea][nodeb]  &&  (nodea != nodeb)) {
      // add an edge from nodea to nodeb
      adj[nodea][nodeb] = true;
      numedges++;
      weight[numedges] = (int)(minweight + 
            ran.nextDouble() * (maxweight + 1 - minweight));
      nodei[numedges] = nodea;
      nodej[numedges] = nodeb;
    }
  }
}


public static int randomRegularGraph(int n, int degree, 
                     long seed, int nodei[], int nodej[])
{
  int i,j,numedges,p,q,r=0,s=0,u,v=0;
  int permute[] = new int[n + 1];
  int deg[] = new int[n + 1];
  boolean adj[][] = new boolean[n+1][n+1];
  boolean more;
  Random ran = new Random(seed);

  // initialize the adjacency matrix
  for (i=1; i<=n; i++)
    for (j=1; j<=n; j++)
      adj[i][j] = false;
  // initialize the degree of each node
  for (i=1; i<=n; i++)
    deg[i] = 0;
  // check input data consistency
  if ((degree % 2) != 0)
    if ((n % 2) != 0) return 1;
  if (n <= degree) return 2;
  // generate the regular graph
  iterate:
  while (true) {
    randomPermutation(n,ran,permute);
    more = false;
    // find two non-adjacent nodes each has less than required degree
    u = 0;
    for (i=1; i<=n; i++)
      if (deg[permute[i]] < degree) {
         v = permute[i];
         more = true;
         for (j=i+1; j<=n; j++) {
           if (deg[permute[j]] < degree) {
             u = permute[j];
             if (!adj[v][u]) {
               // add edge (u,v) to the random graph
               adj[v][u] = adj[u][v] = true;
               deg[v]++;
               deg[u]++;
               continue iterate;
             }
             else {
               // both r & s are less than the required degree
               r = v;
               s = u;
             }
           }
         }
      }
    if (!more) break;
    if (u == 0) { 
      r = v;
      // node r has less than the required degree,
      // find two adjacent nodes p and q non-adjacent to r.
      for (i=1; i<=n-1; i++) {
        p = permute[i];
        if (r != p)
          if (!adj[r][p])
            for (j=i+1; j<=n; j++) {
              q = permute[j];
              if (q != r)
                if (adj[p][q] && (!adj[r][q])) {
                  // add edges (r,p) & (r,q), delete edge (p,q)
                  adj[r][p] = adj[p][r] = true;
                  adj[r][q] = adj[q][r] = true;
                  adj[p][q] = adj[q][p] = false;
                  deg[r]++;
                  deg[r]++;
                  continue iterate;
                }
            }
      }
    }
    else {  
      // nodes r and s of less than required degree, find two
      // adjacent nodes p & q such that (p,r) & (q,s) are not edges.
      for (i=1; i<=n; i++) {
        p = permute[i];
        if ((p != r) && (p != s))
          if (!adj[r][p])
            for (j=1; j<=n; j++) {
              q = permute[j];
              if ((q != r) && (q != s))
                if (adj[p][q] && (!adj[s][q])) {
                  // remove edge (p,q), add edges (p,r) & (q,s) 
                  adj[p][q] = adj[q][p] = false;
                  adj[r][p] = adj[p][r] = true;
                  adj[s][q] = adj[q][s] = true;
                  deg[r]++;
                  deg[s]++;
                  continue iterate;
                }
            }
      }
    }
  }
  numedges = 0;
  for (i=1; i<=n; i++)
    for (j=i+1; j<=n; j++)
      if (adj[i][j]) {
        numedges++;
        nodei[numedges] = i;
        nodej[numedges] = j;
      }
  return 0;
}


public static void randomPermutation(int n, Random ran, int perm[])
{
  int i,j,k;
    
  for (i=1; i<=n; i++)
    perm[i] = i;
  for (i=1; i<=n; i++) {
    j = (int)(i + ran.nextDouble() * (n + 1 - i));
    k = perm[i];
    perm[i] = perm[j];
    perm[j] = k;
  }
}


public static void randomSpanningTree(int n, long seed,
        boolean weighted, int minweight, int maxweight, 
        int nodei[], int nodej[], int weight[])
{
  int nodea,nodeb,numedges;
  int permute[] = new int[n + 1];
  Random ran = new Random(seed);

  // generate a random permutation of n objects
  randomPermutation(n,ran,permute);

  numedges = 0;
  // add n-1 random edges by the greedy method
  for (nodea=2; nodea<=n; nodea++) {
    nodeb = ran.nextInt(nodea - 1) + 1;
    numedges++;
    nodei[numedges] = permute[nodea];
    nodej[numedges] = permute[nodeb];
    if (weighted)
      weight[numedges] = (int)(minweight + 
            ran.nextDouble() * (maxweight + 1 - minweight));
  }
}


public static void randomUnlabeledRootedTree(int n, long seed, int sol[])
{
  int count,v,total,prod,curnum,nextlastroot,numt;
  int stackcounta,stackcountb,prob,p,q,r;
  int rightroot=0,u=0,w=0;
  int numtrees[] = new int[n + 1];
  int aux1[] = new int[n+1];
  int aux2[] = new int[n+1];
  boolean iter;
  Random ran = new Random(seed);

  // calculate numtrees[p], the number of trees on p nodes
  count = 1;
  numtrees[1] = 1;
  while (n > count) {
    total = 0;
    for (p=1; p<=count; p++) {
      q = count + 1;
      prod = numtrees[p] * p;
      for (r=1; r<=count; r++) {
        q -= p;
        if (q <= 0) break;
        total += numtrees[q] * prod;
      }
    }
    count++;
    numtrees[count] = total / (count - 1);
  }
  curnum = n;
  stackcounta = 0;
  stackcountb = 0;
  while (true) {
    if(curnum <= 2) {
      // a new tree placed in "sol", link to left neighbor
      sol[stackcountb+1] = rightroot;
      rightroot = stackcountb + 1;
      stackcountb += curnum;
      if (curnum > 1) sol[stackcountb] = stackcountb - 1;
      while (true) {
        curnum = aux2[stackcounta];
        if (curnum != 0) break;
        // stack counter is decremented as (u,v) is read
        u = aux1[stackcounta];
        stackcounta--;
        w = stackcountb - rightroot + 1;
        nextlastroot = sol[rightroot];
        numt = rightroot + (u - 1) * w - 1;
        if (u != 1) {
          // make u copies of the last tree
          for (p=rightroot; p<=numt; p++) {
            sol[p+w] = sol[p] + w;
            if((p-1)-((p-1)/w)*w == 0) sol[p+w] = nextlastroot;
          }
        }
        stackcountb = numt + w;
        if (stackcountb == n) return;
        rightroot = nextlastroot;      
      }
      aux2[stackcounta] = 0;
      continue;
    }
    // choose a pair (u,v) with a priori probability
    prob = (int)((curnum - 1) * numtrees[curnum] * ran.nextDouble());
    v = 0;
    iter = true;
    while (iter) {
      v++;
      prod = v * numtrees[v];
      w = curnum;
      u = 0;
      iter = false;
      do {
        u++;
        w -= v;
        if (w < 1) {
          iter = true;
          break;
        }
        prob -= numtrees[w] * prod;
      } while (prob >= 0);
    }
    stackcounta++;
    aux1[stackcounta] = u;
    aux2[stackcounta] = v;
    curnum = w;
  }
}


public static void shortestPathTree(int n, int m, int nodei[], int nodej[],
                  int weight[], int root, int mindistance[], int treearc[])
{
  int i,j,k,large,nodeu,nodev,nodey,start,index,last,p,lensum,lenu;
  int queue[] = new int[n+1];
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+1];
  int origin[] = new int[m+1];
  boolean mark[] = new boolean[n+1];

  // obtain a large number greater than all edge weights
  large = 1;
  for (i=1; i<=m; i++)
    large += (weight[i] > 0) ? weight[i] : 0;

  // set up the forward star representation of the graph
  k = 0;
  for (i=1; i<=n; i++) {
    firstedges[i] = k + 1;
    for (j=1; j<=m; j++) {
      if (nodei[j] == i) {
        k++;
        origin[k] = j;
        endnode[k] = nodej[j];
      }
    }
  }
  firstedges[n+1] = m + 1;
  for (i=1; i<=n; i++) {
    treearc[i] = 0;
    mark[i] = true;
    mindistance[i] = large;
  }
  mindistance[root] = 0;
  nodev = 1;
  nodey = nodev;
  nodeu = root;
  while (true) {
    lenu = mindistance[nodeu];
    start = firstedges[nodeu];
    if (start != 0) {
      index = nodeu + 1;
      while (true) {
        last = firstedges[index] - 1;
        if (last > -1) break;
        index++;
      }
      for (i=start; i<=last; i++) {
        p = endnode[i];
        lensum = weight[origin[i]] + lenu;
        if (mindistance[p] > lensum) {
          mindistance[p] = lensum;
          treearc[p] = nodeu;
          if (mark[p]) {
            mark[p] = false;
            queue[nodey] = p;
            nodey++;
            if (nodey > n) nodey = 1;
          }
        }
      }
    }
    if (nodev == nodey) break;
    nodeu = queue[nodev];
    mark[nodeu] = true;
    nodev++;
    if (nodev > n) nodev = 1;
  }
}


public static void stronglyConnectedComponents(int n, int m, int nodei[],
                                            int nodej[], int component[])
{
  int i,j,k,series,stackpointer,numcompoents,p,q,r;
  int backedge[] = new int[n+1];
  int parent[] = new int[n+1];
  int sequence[] = new int[n+1];
  int stack[] = new int[n+1];
  int firstedges[] = new int[n+2];
  int endnode[] = new int[m+1];
  boolean next[] = new boolean[n+1];
  boolean trace[] = new boolean[n+1];
  boolean fresh[] = new boolean[m+1];
  boolean skip,found;

  // set up the forward star representation of the graph
  firstedges[1] = 0;
  k = 0;
  for (i=1; i<=n; i++) {
    for (j=1; j<=m; j++)
      if (nodei[j] == i) {
        k++;
        endnode[k] = nodej[j];
      }
    firstedges[i+1] = k;
  }
  for (j=1; j<=m; j++)
    fresh[j] = true;
  // initialize
  for (i=1; i<=n; i++) {
    component[i] = 0;
    parent[i] = 0;
    sequence[i] = 0;
    backedge[i] = 0;
    next[i] = false;
    trace[i] = false;
  }
  series = 0;
  stackpointer = 0;
  numcompoents = 0;
  // choose an unprocessed node not in the stack
  while (true) {
    p = 0;
    while (true) {
      p++;
      if (n < p) {
        component[0] = numcompoents;
        return;
      }
      if (!trace[p]) break;
    }
    series++;
    sequence[p] = series;
    backedge[p] = series;
    trace[p] = true;
    stackpointer++;
    stack[stackpointer] = p;
    next[p] = true;
    while (true) {
      skip = false;
      for (q=1; q<=n; q++) {
        // find an unprocessed edge (p,q)
        found = false;
        for (i=firstedges[p]+1; i<=firstedges[p+1]; i++)
          if ((endnode[i] == q) && fresh[i]) {
            // mark the edge as processed
            fresh[i] = false;
            found = true;
            break;
          }
        if (found) {
          if (!trace[q]) {
            series++;
            sequence[q] = series;
            backedge[q] = series;
            parent[q] = p;
            trace[q] = true;
            stackpointer++;
            stack[stackpointer] = q;
            next[q] = true;
            p = q;
          }
          else {
            if (trace[q]) {
              if (sequence[q] < sequence[p] && next[q]) {
                backedge[p] = (backedge[p] < sequence[q]) ? 
                               backedge[p] : sequence[q];
              }
            }
          }
          skip = true;
          break;
        }  
      }
      if (skip) continue;
      if (backedge[p] == sequence[p]) {
        numcompoents++;
        while (true) {
          r = stack[stackpointer];
          stackpointer--;
          next[r] = false;
          component[r] = numcompoents;
          if (r == p) break;
        }
      }
      if (parent[p] != 0) {
        backedge[parent[p]] = (backedge[parent[p]] < backedge[p]) ? 
                               backedge[parent[p]] : backedge[p];
        p = parent[p];
      }
      else
        break;
    }
  }
}


public static void travelingSalesmanProblem(int n, int dist[][], int sol[])
{
  int i, p;
  int row[] = new int[n + 1];
  int column[] = new int[n + 1];
  int front[] = new int[n + 1];
  int cursol[] = new int[n + 1];
  int back[] = new int[n + 1];

  for (i=1; i<=n; i++) {
    row[i] = i;  
    column[i] = i;
    front[i] = 0;
    back[i] = 0;
  }
  dist[0][0] = Integer.MAX_VALUE;
  tspsearch(n, 0, 0, dist, row, column, cursol, front, back);
  p = 1;
  for (i=1; i<=n; i++) {
    sol[i] = p;
    p = cursol[p];
  }
}


static private void cpt_DuplicateEdges(int neighbor[], int next[], 
                                 int idxb, int idxc, int tmparg[])
{
  /* this method is used internally by ChinesePostmanTour */

  // Duplicate matching edges

  int p,q,r;

  p = tmparg[0];
  q = idxb;
  r = idxc;
  while (true) {
    p = next[q];
    while (true) {
      if (neighbor[p] == r) break;
      p++;
    }
    neighbor[p] = -r;
    if (q == idxc) break;
    q = idxc;
    r = idxb;
  }
  tmparg[0] = p;
}


static private void cpt_ExpandBlossom(int core[], int aux1[], int aux3[],
                        float wk1[], float wk2[], int tmparg[], int idxd)
{
  /* this method is used internally by ChinesePostmanTour */

  // Expanding a blossom

  int p,q,r;
  float work;

  r = tmparg[0];
  p = r;
  do {
    r = p;
    q = aux3[r];
    work = wk1[r];
    while (true) {
      core[p] = r;
      wk2[p] -= work;
      if (p == q) break;
      p = aux1[p];
    }
    p = aux1[q];
    aux1[q] = r;
  } while (p != idxd);
  tmparg[0] = r;
}


static private void cpt_FirstScan(int neighbor[], int weight[], int next[],
              int core[], int aux1[], int aux2[], int aux3[], int aux4[],
              float wk1[], float wk2[], float wk3[], float wk4[], int locb)
{
  /* this method is used internally by ChinesePostmanTour */

  // Node scanning

  int i,p,q,r,s,t,u,v;
  float work1,work2,work3,work4,work5;

  work3 = wk3[locb] - wk1[locb];
  q = locb;
  r = aux4[locb];
  t = -1;
  if (r > 0) t = core[r];
  do {
    i = next[q];
    v = next[q+1] - 1;
    work1 = wk2[q];
    for (p=i; p<=v; p++) {
      s = neighbor[p];
      u = core[s];
      if (locb != u) {
        if (t != u) {
          work4 = wk4[u];
          work2 = wk1[u] + wk2[s];
          work5 = (float)(weight[p]);
          work5 += work3 - work1 - work2;
          if (work4 > work5) {
            wk4[u] = work5;
            aux2[u] = q;
            aux3[u] = s;
          }
        }
      }
    }
    q = aux1[q];
  } while (q != locb);
}  


static private void cpt_SecondScan(int neighbor[], int weight[],
       int next[], int high, int core[], int aux1[], int aux2[],
       int aux3[], int aux4[], float wk1[], float wk2[],
       float wk3[], float wk4[], int tmparg[], int v)
{
  /* this method is used internally by ChinesePostmanTour */

  // Node scanning

  int i,p,q,r,s,t,u;
  float work1,work2,work3,work4,work5;

  u = tmparg[0];
  do {
    r = core[u];
    if (r == u) {
      work4 = high;
      work2 = wk1[u];
      do {
        i = next[r];
        s = next[r+1] - 1;
        work1 = wk2[r];
        for (p=i; p<=s; p++) {
          q = neighbor[p];
          t = core[q];
          if (t != u) {
            if (aux4[t] >= 0) {
              work3 = wk3[t] - wk1[t] - wk2[q];
              work5 = (float)(weight[p]);
              work5 += work3 - work2 - work1;
              if (work4 > work5) {
                work4 = work5;
                aux2[u] = q;
                aux3[u] = r;
              }
            }
          }
        }
        r = aux1[r];
      } while (r != u);
      wk4[u] = work4;
    }
    u++;
  } while (u <= v);
  tmparg[0] = u;
}


static private void cpt_ShrinkBlossom(int core[], int aux1[], 
            float wk1[], float wk2[], int locb, int tmparg[])
{
  /* this method is used internally by ChinesePostmanTour */

  // Shrinking of a blossom

  int p,q,r;
  float work;

  p = tmparg[0];
  q = p;
  work = wk1[p];
  while (true) {
    core[p] = locb;
    wk2[p] += work;
    r = aux1[p];
    if (r == q) {
      tmparg[0] = p;
      return;
    }
    p = r;
  }
}


static private void cpt_Trail(int n, int neighbor[], int weight[],
                int next[], int aux3[], int core[], int startnode)
{
  /* this method is used internally by ChinesePostmanTour */

  // Determine an Eulerian trail

  int i,nplus,p,q,r,t,u,v;
  boolean finish;

  nplus = n + 1;
  u = next[nplus];
  if (startnode <= 0 || startnode > n) startnode = 1;
  for (p=1; p<=n; p++) {
    i = next[p] - 1;
    aux3[p] = i;
    core[p] = i;
  }
  p = startnode;
  iterate:
  while (true) {
    i = core[p];
    while (true) {
      v = next[p+1] - 1;
      while (true) {
        i++;
        if (i > v) break;
        q = neighbor[i];
        if (q > n) continue;
        if (q >= 0) {
          t = core[q];
          do {
            t++;
          } while (neighbor[t] != p);
          neighbor[t] = nplus;
          t = aux3[q] + 1;
          aux3[q] = t;
          weight[t] = p;
          core[p] = i;
          p = q;
          continue iterate;
        }
        r = -p;
        q= -q;
        t = core[q];
        do {
          t++;
        } while (neighbor[t] != r);
        neighbor[t] = nplus;
        t = aux3[q] + 1;
        aux3[q] = t;
        weight[t] = p;
        t = aux3[p] + 1;
        aux3[p] = t;
        weight[t] = q;
      }
      core[p] = u;
      finish = true;
      for (p=1; p<=n; p++) {
        i = core[p];
        t = aux3[p];
        if ((t >= next[p]) && (i < u)) {
          finish = false;
          break;
        }
      }
      if (finish) return;
    }
  }
}


static private boolean isomorphicCode(int adj[][], int n, 
                                      int label[][], int map[])
{
  /* this method is used internally by graphIsomorphism */  

  int i,j,k,auxsize,flag,p,q,r,s,t,v;
  int ctr1[] = new int[1];
  int ctr2[] = new int[1];
  int ctr3[] = new int[1];
  int ctr4[] = new int[1];
  int savelabel[][] = new int[n+1][n+1];
  int savemap[] = new int[n+1];
  int aux1[] = new int[n+1];
  int aux2[] = new int[n+1];
  int aux3[] = new int[n+1];
  int aux4[] = new int[4 * n+1];
  boolean found;

  aux4[1] = 0;
  ctr3[0] = 0;
  ctr4[0] = 0;
  auxsize = 4 * n;
  // initial identity ordering
  for (i=1; i<=n; i++)
    map[i] = i;
  // compute the code
  for (i=1; i<=n; i++) {
    if (i <= n) {
      p = map[i];
      if (map[i] < 1 || n < map[i]) return false;
    }
    else
      p = 0;
    for (j=i+1; j<=n; j++) {
      if (j <= n) {
        q = map[j];
        if (map[j] < 1 || n < map[j]) return false;
      }
      else
        q = 0;
      if (p == 0 || q == 0)
        label[i][j] = label[j][i] = 0;
      else if ((p < q && adj[p][q] != 0 ) || (q < p && adj[q][p] != 0 ))      
        label[i][j] = label[j][i] = 1;
      else
        label[i][j] = label[j][i] = 0;
    }
  }
  // save the current best ordering and code
  for (i=1; i<=n; i++) {
    for (j=1; j<=n; j++)
      savelabel[i][j] = label[i][j];
    savemap[i] = map[i];
  }
  // begin backtrack search
  // consider all possible orderings and their codes
  ctr1[0] = 0;
  while (true) {
    // construct the integer vector by backtracking
    if (ctr1[0] == 0) {
      // process the complete vector
      ctr2[0] = 1;
      ctr4[0] = 0;
      ctr1[0] = 2;
    }
    else {
      // examine the stack
      while (true) {
        if (0 < aux1[ctr2[0]]) {
          // take the first available one off the stack
          map[ctr2[0]] = aux4[ctr4[0]];
          ctr4[0]--;
          aux1[ctr2[0]]--;
          if (ctr2[0] != n) {
            ctr2[0]++;
            ctr1[0] = 2;
          }
          else
            ctr1[0] = 1;
          break;
        }
        else {
          // there are no candidates for position ctr2[0]
          ctr2[0]--;
          if (ctr2[0] <= 0) {
            // repeat the examination of the stack 
            ctr1[0] = 3;
            break;
          }
        }
      }
    }
    // if the backtrack routine has returned a complete candidate
    // ordering, then compute the resulting code, and compare with
    // the current best and go back for the next backtrack search
    if (ctr1[0] == 1) {
      // compute the code
      for (i=1; i<=n; i++) {
        if (i <= n) {
          p = map[i];
          if (map[i] < 1 || n < map[i]) return false;
        }
        else
          p = 0;
        for (j=i+1; j<=n; j++) {
          if (j <= n) {
            q = map[j];
            if (map[j] < 1 || n < map[j]) return false;
          }
          else
            q = 0;
          if (p == 0 || q == 0)
            label[i][j] = label[j][i] = 0;
          else if ((p < q && adj[p][q] != 0 ) || (q < p && adj[q][p] != 0 ))      
            label[i][j] = label[j][i] = 1;
          else
            label[i][j] = label[j][i] = 0;
        }
      }
      // compare savelabel and code
      flag = 0;
      for (j=2; j<=n; j++) {
        for (i=1; i<=j-1; i++) 
          if (savelabel[i][j] < label[i][j]) {
            flag = - 1;
            break;
          }
          else if (label[i][j] < savelabel[i][j]) {
            flag = 1;
            break;
          }
        if (flag != 0) break;
      }
      ctr3[0]++;
      if (flag == -1) {
        for (i=1; i<=n; i++) {
          for (j=1; j<=n; j++)
            savelabel[i][j] = label[i][j];
          savemap[i] = map[i];
        }
      }
    }
    else if (ctr1[0] == 2) {
      // finds candidates for a maximal graph code ordering
      if (ctr2[0] < 1 || n < ctr2[0]) return false;
      aux1[ctr2[0]] = 0;
      found = false;
      if (1 < ctr2[0]) {
        // compute the graph code for this node ordering
        for (i=1; i<=n; i++) {
          if (i <= ctr2[0]-1) {
            p = map[i];
            if (map[i] < 1 || n < map[i]) return false;
          }
          else
            p = 0;
          for (j=i+1; j<=n; j++) {
            if (j <= ctr2[0]-1) {
              q = map[j];
              if (map[j] < 1 || n < map[j]) return false;
            }
            else
              q = 0;
            if (p == 0 || q == 0)
              label[i][j] = label[j][i] = 0;
            else if ((p < q && adj[p][q] != 0) || (q < p && adj[q][p] != 0))
              label[i][j] = label[j][i] = 1;
            else
              label[i][j] = label[j][i] = 0;
          }
        }
        // compares the two graph codes
        flag = 0;
        for (j=2; j<=ctr2[0]-1; j++) {
          for (i=1; i<=j-1; i++) {
            if (savelabel[i][j] < label[i][j]) {
              flag = - 1;
              break;
            }
            else if (label[i][j] < savelabel[i][j]) {
              flag = + 1;
              break;
            }
          }
          if (flag != 0) break;
        }
        ctr3[0]++;
        if (flag == 1) {
          aux1[ctr2[0]] = 0;
          found = true;
        }
      }
      if (!found) {
        // list of nodes that have not been used
        t = n + 1 - ctr2[0];
        // find the number of unused items in the permutation
        v = ctr2[0] - 1 + t;
        if ( ctr2[0] - 1 < 0 )
          return false;
        else if (ctr2[0]-1 == 0)
          for (i=1; i<=v; i++)
            aux2[i] = i;
        else if (t < 0)
          return false;
        else if (t == 0)
          {}
        else {
          k = 0;
          for (i=1; i<=v; i++) {
            r = 0;
            for (j=1; j<=ctr2[0]-1; j++) 
              if (map[j] == i) {
                r = j;
                break;
              }
            if (r == 0) {
              k++;
              if (t < k) return false;
              aux2[k] = i;
            }
          }
        }
        aux1[ctr2[0]] = 0;
        for (i=1; i<=ctr2[0]-1; i++) {
          p = map[i];
          for (j=1; j<=t; j++) {
            q = aux2[j];
            if (adj[p][q] != 0 || adj[q][p] != 0) {
              aux1[ctr2[0]]++;
              ctr4[0]++;
              if (auxsize < ctr4[0]) return false;
              aux4[ctr4[0]] = q;
            }
          }
          if (0 < aux1[ctr2[0]]) {
            found = true;
            break;
          }
        }
        if (!found) {
          // no free nodes are connected to used nodes
          // take the free nodes with at least one neighbor
          s = 0;
          for (i=1; i<=t; i++) {
            p = aux2[i];
            aux3[i] = 0;
            for (j=1; j<=t; j++) {
              q = aux2[j];
              if (p != q)
                if (aux3[i] < adj[p][q]) aux3[i] = adj[p][q];
            }
            if (s < aux3[i]) s = aux3[i];
          }
          aux1[ctr2[0]] = 0;
          for (i=1; i<=t; i++)
            if (aux3[i] == s) {
              aux1[ctr2[0]]++;
              ctr4[0]++;
              if (auxsize < ctr4[0]) return false;
              aux4[ctr4[0]] = aux2[i];
            }
        }
      }
    }
    else
      // all possibilities have been examined
      break;
  }
  // set the best ordering and code
  for (i=1; i<=n; i++) {
    for (j=1; j<=n; j++)
      label[i][j] = savelabel[i][j];
    map[i] = savemap[i];
  }
  return true;
}


static private void msmSubprograma(int kk, int n, double big,
           double cost[], int aux1[], int aux2[], int aux3[], 
           int aux4[], int aux5[], int aux6[], int aux8[], 
           double work1[], double work2[], double work3[],
           double work4[])
{
  /* this method is used internally by minSumMatching */

  int i,head,j,jj1,jj2,jj3,jj4,min,max,sub;
  double cswk,cstwk,xcst,xwk2,xwk3;

  head = n + 2;
  do {
    jj1 = kk;
    kk = aux8[jj1];
    aux8[jj1] = head;
    cstwk = big;
    jj3 = 0;
    jj4 = 0;
    j = jj1;
    xwk2 = work2[jj1];
    do {
      xwk3 = work3[j];
      for (i=1; i<=n; i++) {
        jj2 = aux2[i];
        if (aux6[jj2] < head) {
          min = j;
          max = i;
          if (j != i) {
            if (j > i) {
              max = j;
              min = i;
            }
            sub = aux1[max] + min;
            xcst = cost[sub];
            cswk = cost[sub] - xwk2 - xwk3;
            cswk -= (work2[jj2] + work3[i]);
            cswk += work4[jj2];
            if (cswk < cstwk) {
              jj3 = i;
              jj4 = j;
              cstwk = cswk;
            }
          }
        }
      }
      j = aux3[j];
    } while (j != jj1);
    aux4[jj1] = jj3;
    aux5[jj1] = jj4;
    work1[jj1] = cstwk;
  } while (kk != 0);
}


static private void msmSubprogramb(int kk, int n, double big,
           double cost[], int aux1[], int aux2[], int aux3[],
           int aux4[], int aux5[], int aux7[], int aux9[],
           double work1[], double work2[], double work3[],
           double work4[])
{
  /* this method is used internally by minSumMatching */

  int i,ii,head,jj1,jj2,jj3,min,max,sub;
  double cswk,xcst,xwk1,xwk2;

  head = n + 2;
  xwk1 = work4[kk] - work2[kk];
  work1[kk] = big;
  xwk2 = xwk1 - work3[kk];
  aux7[kk] = 0;
  ii = 0;
  for (i=1; i<=n; i++) {
    jj3 = aux2[i];
    if (aux7[jj3] >= head) {
      ii++;
      aux9[ii] = i;
      min = kk;
      max = i;
      if (kk != i) {
        if (kk > i) {
          max = kk;
          min = i;
        }
        sub = aux1[max] + min;
        cswk = cost[sub] + xwk2;
        cswk -= (work2[jj3] + work3[i]);
        if (cswk < work1[jj3]) {
          aux4[jj3] = kk;
          aux5[jj3] = i;
          work1[jj3] = cswk;
        }
      }
    }
  }
  aux7[kk] = head;
  jj1 = kk;
  jj1 = aux3[jj1];
  if (jj1 == kk) return;
  do {
    xwk2 = xwk1 - work3[jj1];
    for (i=1; i<=ii; i++) {
      jj2 = aux9[i];
      jj3 = aux2[jj2];
      min = jj1;
      max = jj2;
      if (jj1 != jj2) {
        if (jj1 > jj2) {
          max = jj1;
          min = jj2;
        }
        sub = aux1[max] + min;
        xcst = cost[sub];
        cswk = cost[sub] + xwk2;
        cswk -= (work2[jj3] + work3[jj2]);
        if (cswk < work1[jj3]) {
          aux4[jj3] = jj1;
          aux5[jj3] = jj2;
          work1[jj3] = cswk;
        }
      }
    }
    jj1 = aux3[jj1];
  } while (jj1 != kk);
}


static private void minimalEqGraphFindp(int n, int m, int n1, int nodeu,
       int nodev, int endnode[], int firstedges[], boolean currentarc[],
       boolean pexist[], int nextnode[], boolean pathexist[])
{
  /* this method is used internally by minimalEquivalentGraph */

  // determine if a path exists from nodeu to nodev by Yen's algorithm

  int i,j,k,i2,j2,low,up,kedge=0,index1,index2,index3;
  boolean join;

  // initialization
  for (i=1; i<=n; i++) {
    nextnode[i] = i;
    pathexist[i] = false;
  }
  pathexist[nodeu] = true;
  nextnode[nodeu] = n;
  index1 = nodeu;
  index2 = n - 1;
  // compute the shortest distance labels
  i = 1;
  while (true) {
    j = nextnode[i];
    join = false;
    low = firstedges[index1];
    up = firstedges[index1 + 1];
    if (up > low) {
      up--;
      for (k=low; k<=up; k++)
        if (endnode[k] == j) {
          join = true;
          kedge = k;
          break;
        }
    }
    if (join)
      if (currentarc[kedge]) pathexist[j] = true;
    if (pathexist[j]) {
      index3 = i + 1;
      if (index3 <= index2) {
        for (i2=index3; i2<=index2; i2++) { 
          j2 = nextnode[i2];
          join = false;
          low = firstedges[index1];
          up = firstedges[index1 + 1];
          if (up > low) {
            up--;
            for (k=low; k<=up; k++)
              if (endnode[k] == j2) {
                join = true;
                kedge = k;
                break;
              }
          }
          if (join)
            if (currentarc[kedge]) pathexist[j2] = true;
        }
      }
      // check whether an alternative path exists
      if (pathexist[nodev]) {
         pexist[0] = true;
         return;
      }
      nextnode[i] = nextnode[index2];
      index1 = j;
      index2--;
      if (index2 > 1) continue;
      join = false;
      low = firstedges[index1];
      up = firstedges[index1 + 1];
      if (up > low) {
        up--;
        for (k=low; k<=up; k++)
          if (endnode[k] == nodev) {
            join = true;
            kedge = k;
            break;
          }
      }
      pexist[0] = false;
      if (join)
        if (currentarc[kedge]) pexist[0] = true;
      return;
    }
    i++;
    if (i <= index2) continue;
    pexist[0] = false;
    return;
  }
}


static private void mkpsCurrentUpperBound(int n, int m, int profit[],
       int weight[], int aux2[], int i, int netp, int res1[], 
       int res3[], int aux5[], int aux7[], int aux8[], int aux9[],
       int aux10[], int aux11[])
{
  /* this method is used internally by multipleKnapsack */

  // Compute an upper bound of the current solution

  int j, q, wk1, wk2;
  int ref1[] = new int[1];

  wk1 = 0;
  ref1[0] = 0;
  for (j=i; j<=m; j++)
    ref1[0] += aux2[j];
  wk2 = 0;
  for (j=1; j<=n; j++) {
    aux5[j] = 0;
    if (aux9[j] != 0) {
      wk1++;
      aux7[wk1] = j;
      aux10[wk1] = profit[j];
      aux11[wk1] = weight[j];
      wk2 += weight[j];
    }
  }
  if (wk2 <= ref1[0]) {
    res1[0] = ref1[0] - wk2;
    res3[0] = 0;
    if (wk1 == 0) return;
    for (j=1; j<=wk1; j++) {
      res3[0] += aux10[j];
      aux8[j] = 1;
    }
  }
  else {
    // compute the solution with one knapsack
    mkpsSingleKnapsack(n,wk1,ref1,netp,res3,aux8,aux10,aux11);
    res1[0] = ref1[0];
  }
  for (j=1; j<=wk1; j++) {
    q = aux7[j];
    aux5[q] = aux8[j];
  }
}

    
static private void mkpsFeasibleSolution(int n, int m, int profit[],
       int weight[], int i, int netp, int res2[], int aux1[], int aux2[],
       int aux3[], int aux7[], int aux8[], int aux9[], int aux10[],
       int aux11[], int aux12[][], int aux14[][], int aux15[][])
{
  /* this method is used internally by multipleKnapsack */

  // Get a feasible solution

  int p,j,q,netpa,accu1,accu2,accu3,accu4,accu5;
  int ref1[] = new int[1];
  int pb[] = new int[1];

  accu5 = 0;
  for (j=1; j<=n; j++)
    if (aux9[j] != 0) {
      accu5++;
      aux7[accu5] = j;
    }
  for (j=i; j<=m; j++) {
    aux1[j] = 0;
    aux3[j] = 0;
  }
  res2[0] = 0;
  netpa = netp;
  if (accu5 == 0) return;
  accu3 = 0;
  accu4 = 0;
  for (j=1; j<=accu5; j++) {
    q = aux7[j];
    if (aux12[i][q] == 0) {
      if (weight[q] <= aux2[i]) {
        accu3++;
        accu4 += weight[q];
        aux15[i][accu3] = q;
        aux10[accu3] = profit[q];
        aux11[accu3] = weight[q];
      }
    }
  }
  p = i;
  while (true) {
    aux1[p] = accu3;
    if (accu4 <= aux2[p]) {
      pb[0] = 0;
      if (accu3 != 0) {
        for (j=1; j<=accu3; j++) {
          pb[0] += aux10[j];
          aux14[p][j] = 1;
        }
      }
    }
    else {
      ref1[0] = aux2[p];
      netp = 0;
      if (p == m) netp = netpa;
      // compute the solution with one knapsack
      mkpsSingleKnapsack(n,accu3,ref1,netp,pb,aux8,aux10,aux11);
      for (j=1; j<=accu3; j++)
        aux14[p][j] = aux8[j];
    }
    res2[0] += pb[0];
    netpa -= pb[0];
    aux3[p] = pb[0];
    aux15[p][accu3+1] = n + 1;
    if (p == m) return;
    accu1 = 1;
    accu2 = 0;
    for (j=1; j<=accu5; j++) {
      if (aux7[j] >= aux15[p][accu1]) {
        accu1++;
        if (aux14[p][accu1-1] == 1) continue;
      }
      accu2++;
      aux7[accu2] = aux7[j];
    }
    accu5 = accu2;
    if (accu5 == 0) return;
    accu3 = 0;
    accu4 = 0;
    p++;
    for (j=1; j<=accu5; j++) {
      q = aux7[j];
      if(weight[q] <= aux2[p]) {
        accu3++;
        accu4 += weight[q];
        aux15[p][accu3] = q;
        aux10[accu3] = profit[q];
        aux11[accu3] = weight[q];
      }
    }
  }
}


static private void mkpsCalculateBound(int i, int p, int res3[],
       boolean control[], int slackbnd, int upperbnd, int indexj,
       int indexi, int n, int res1[], int res1a, int ubtmp,
       int aux0[], int aux2[], int aux5[], int aux6[], int aux9[],
       int aux12[][])
{
  /* this method is used internally by multipleKnapsack */

  // Upper bound computation

  int j,id1,id2,id3,id4;
   
  control[0] = false;
  if (aux9[indexj] == 0) {
    id1 = i - 1;
    if (id1 >= indexi) {
      id2 = 0;
      for (j=indexi; j<=id1; j++)
        id2 += aux2[j];
      if (id2 > res1[0]) return;
    }
    id3 = p;
    id4 = aux0[id3];
    while (true) {
      if (id4 == -1) {
        id3--;
        id4 = aux0[id3];
      }
      else {
        if (aux5[id4] == 0) return;
        if (id4 == indexj) break;
        id4 = aux12[id3][id4];
      }
    }
    res3[0] = upperbnd - slackbnd;
    control[0] = true;
    return;
  }
  id1 = i - 1;
  if (id1 >= 1) {
    id2 = 0;
    for (j=1; j<=id1; j++)
      id2 += aux2[j];
    if (id2 > res1a) return;
  }
  for (j=1; j<=n; j++)
    if (aux9[j] != 1)
      if (aux6[j] == 0) return;
  res3[0] = ubtmp - slackbnd;
  control[0] = true;
}


static private void mkpsSingleKnapsack(int n, int ns, int ref1[],
       int netp,int ref2[], int aux8[], int aux10[], int aux11[])
{
  /* this method is used internally by multipleKnapsack */

  // Compute the solution with one knapsack

  int p,p1,in,j,j1,q,diff,index2,index3,index4,index5,index6;
  int thres,thres1,val2,prev,n1,r;
  int val1=0,index1=0,index7=0,t=0;
  float tmp1,tmp2,tmp3;
  int work1[] = new int[n + 1];
  int work2[] = new int[n + 1];
  int work3[] = new int[n + 1];
  int work4[] = new int[n + 1];
  int work5[] = new int[n + 1];
  boolean skip=false, jump=false, middle=false, over=false, outer=false;

  ref2[0] = netp;
  index3 = 0;
  index2 = ref1[0];
  for (j=1; j<=ns; j++) {
    index1 = j;
    if (aux11[j] > index2) break;
    index3 += aux10[j];
    index2 -= aux11[j];
  }
  index1--;
  if (index2 != 0) {
    aux10[ns+1] = 0;
    aux11[ns+1] = ref1[0] + 1;
    thres = index3 + index2 * aux10[index1+2] / aux11[index1+2];
    tmp1 = index3 + aux10[index1+1];
    tmp2 = (aux11[index1+1] - index2) * aux10[index1];
    tmp3 = aux11[index1];
    thres1 = (int) (tmp1 - tmp2 / tmp3);
    if (thres1 > thres) thres = thres1;
    if (thres <= ref2[0]) return;
    val2 = ref1[0] + 1;
    work2[ns] = val2;
    for (j=2; j<=ns; j++) {
      index7 = ns + 2 - j;
      if (aux11[index7] < val2) val2 = aux11[index7];
      work2[index7-1] = val2;
    }
    for (j=1; j<=ns; j++)
      work1[j] = 0;
    index5 = 0;
    prev = ns;
    p = 1;
    skip = true;
  }
  else {
    if (ref2[0] >= index3) return;
    ref2[0] = index3;
    for (j=1; j<=index1; j++)
      aux8[j] = 1;
    index6 = index1 + 1;
    for (j=index6; j<=ns; j++)
      aux8[j] = 0;
    ref1[0] = 0;
    return;
  }
  middle = false;
  while (true) {
    if (!skip) {
      if (aux11[p] > ref1[0]) {
        p1 = p + 1;
        if (ref2[0] >= ref1[0] * aux10[p1] / aux11[p1] + index5) {
          middle = true;
        }
        if (!middle) {
          p = p1;
          continue;
        }
      }
      if (!middle) {
        index3 = work3[p];
        index2 = ref1[0] - work4[p];
        in = work5[p];
        index1 = ns;
        if (in <= ns) {
          for (j=in; j<=ns; j++) {
            index1 = j;
            if (aux11[j] > index2) {
              index1--;
              if (index2 == 0) break;
              if (ref2[0] >= index5 + index3 + index2 * aux10[j] / aux11[j]) {
                middle = true;
                break;
              }
              skip = true;        
              break;
            }
            index3 += aux10[j];
            index2 -= aux11[j];
          }
        }
      }
      if (!middle) {
        if (!skip) {
          if (ref2[0] >= index3 + index5) {
            middle = true;
          }
          if (!middle) {
            ref2[0] = index3 + index5;
            val1 = index2;
            index6 = p - 1;
            for (j=1; j<=index6; j++)
              aux8[j] = work1[j];
            for (j=p; j<=index1; j++)
              aux8[j] = 1;
            if (index1 != ns) {
              index6 = index1 + 1;
              for (j=index6; j<=ns; j++)
                aux8[j] = 0;
            }
            if (ref2[0] != thres) {
              middle = true;
            }
            if (!middle) {
              ref1[0] = val1;
              return;
            }
          }
        }
      }
    }
    if (!middle) {
      skip = false;
      work4[p] = ref1[0] - index2;
      work3[p] = index3;
      work5[p] = index1 + 1;
      work1[p] = 1;
      index6 = index1 - 1;
      if (index6 >= p)
        for (j=p; j<=index6; j++) {
          work4[j+1] = work4[j] - aux11[j];
          work3[j+1] = work3[j] - aux10[j];
          work5[j+1] = index1 + 1;
          work1[j+1] = 1;
        }
      j1 = index1 + 1;
      for (j=j1; j<=prev; j++) {
        work4[j] = 0;
        work3[j] = 0;
        work5[j] = j;
      }
      prev = index1;
      ref1[0] = index2;
      index5 += index3;
      if ((index1 - (ns - 2)) > 0) 
        p = ns;
      else if ((index1 - (ns - 2)) == 0) {
        if (ref1[0] >= aux11[ns]) {
          ref1[0] -= aux11[ns];
          index5 += aux10[ns];
          work1[ns] = 1;
        }
        p = ns - 1;
      }
      else {
        p = index1 + 2;
        if (ref1[0] >= work2[p-1]) continue;
      }
      if (ref2[0] < index5) {
        ref2[0] = index5;
        for (j=1; j<=ns; j++)
          aux8[j] = work1[j];
        val1 = ref1[0];
        if (ref2[0] == thres) return;
      }
      if (work1[ns] != 0) {
        work1[ns] = 0;
        ref1[0] += aux11[ns];
        index5 -= aux10[ns];
      }
    }
    outer = false;
    while (true) {
      middle = false;
      index6 = p - 1;
      jump = false;
      if (index6 != 0) {
        for (j=1; j<=index6; j++) {
          index7 = p - j;
          if (work1[index7] == 1) {
            jump = true;
            break;
          }
        }
      }
      if (!jump) {
        ref1[0] = val1;
        return;
      }
      r = ref1[0];
      ref1[0] += aux11[index7];
      index5 -= aux10[index7];
      work1[index7] = 0;
      if (r >= work2[index7]) {
        p = index7 + 1;
        outer = true;
        break;
      }
      index6 = index7 + 1;
      p = index7;
      over = false;
      while (true) {
        if (ref2[0] >= index5 + ref1[0] * aux10[index6] / aux11[index6]) {
          over = true;
          break;
        }
        diff = aux11[index6] - aux11[index7];
        if (diff < 0) {
          t = r - diff;
          if (t < work2[index6]) {
            index6++;
            continue;
          }
          break;
        }
        if (diff == 0) { 
          index6++;
          continue;
        }
        if (diff > r) {
          index6++;
          continue;
        }
        if (ref2[0] >= index5 + aux10[index6]) {
          index6++;
          continue;
        }
        ref2[0] = index5 + aux10[index6];
        for (j=1; j<=index7; j++)
          aux8[j] = work1[j];
        q = index7 + 1;
        for (j=q; j<=ns; j++)
          aux8[j] = 0;
        aux8[index6] = 1;
        val1 = ref1[0] - aux11[index6];
        if (ref2[0] == thres) {
          ref1[0] = val1;
          return;
        }
        r -= diff;
        index7 = index6;
        index6++;
      }
      if (!over) {
        n = index6 + 1; 
        if (ref2[0] < index5 + aux10[index6] + t * aux10[n] / aux11[n])
          break;
      }
      else
        over = false;
    }
    if (!outer) {
      ref1[0] -= aux11[index6];
      index5 += aux10[index6];
      work1[index6] = 1;
      p = index6 + 1;
      work4[index6] = aux11[index6];
      work3[index6] = aux10[index6];
      work5[index6] = p;
      n1 = index6 + 1;
      for (j=n1; j<=prev; j++) {
        work4[j] = 0;
        work3[j] = 0;
        work5[j] = j;
      }
      prev = index6;
    }
    else
      outer = false;
  }
}


static private void planarityDFS1(int n, int m, int m2, int nm2, int level[],
       boolean middle[], int snum[], int store1[], int mark[],
       int firstlow[], int secondlow[], int wkpathfind5[], int wkpathfind6[],
       int stackarc[], int first[], int second[])
{
  /* this method is used internally by planarityTesting */
  
  int pnode=0, qnode=0, tnode=0, tmp1, tmp2;
  boolean skip;

  skip = false;
  if (middle[0]) skip = true;
  if (!skip) {
    qnode = wkpathfind5[level[0]];
    pnode = wkpathfind6[level[0]];
  }
  while (second[qnode] > 0 || skip) {
    if (!skip) {
      tnode = first[second[qnode]];
      second[qnode] = second[second[qnode]];
    }
    if (((mark[tnode] < mark[qnode]) && (tnode != pnode)) || skip) {
      if (!skip) {
        store1[0]+= 2;
        stackarc[store1[0]-1] = qnode;
        stackarc[store1[0]]   = tnode;
      }
      if ((mark[tnode] == 0) || skip) {
        if (!skip) {
          snum[0]++;
          mark[tnode] = snum[0];
          level[0]++;
          wkpathfind5[level[0]] = tnode;
          wkpathfind6[level[0]] = qnode;
          middle[0] = false;
          return;
        }
        skip = false;
        tnode = wkpathfind5[level[0]];
        qnode = wkpathfind6[level[0]];
        level[0]--;
        pnode = wkpathfind6[level[0]];
        if (firstlow[tnode] < firstlow[qnode]) {
          tmp1 = secondlow[tnode];
          tmp2 = firstlow[qnode];
          secondlow[qnode] = (tmp1 < tmp2 ? tmp1 : tmp2);
          firstlow[qnode] = firstlow[tnode];
        }
        else {
          if (firstlow[tnode] == firstlow[qnode]) {
            tmp1 = secondlow[tnode];
            tmp2 = secondlow[qnode];
            secondlow[qnode] = (tmp1 < tmp2 ? tmp1 : tmp2);
          }
          else {
            tmp1 = firstlow[tnode];
            tmp2 = secondlow[qnode];
            secondlow[qnode] = (tmp1 < tmp2 ? tmp1 : tmp2);
          }
        }
      }
      else {
        if (mark[tnode] < firstlow[qnode]) {
          secondlow[qnode] = firstlow[qnode];
          firstlow[qnode] = mark[tnode];
        }
        else {
          if (mark[tnode] > firstlow[qnode]) {
            tmp1 = mark[tnode];
            tmp2 = secondlow[qnode];
            secondlow[qnode] = (tmp1 < tmp2 ? tmp1 : tmp2);
          }
        }
      }
    }
  }
  middle[0] = true;
}


static private void planarityDFS2(int n, int m, int m2, int nm2, int level[], 
       boolean middle[], int snum[], int store1[], int mark[], 
       int wkpathfind5[], int stackarc[], int first[], int second[])
{
  /* this method is used internally by planarityTesting */
  
  int qnode, tnode;

  if (middle[0]) {
    tnode = wkpathfind5[level[0]];
    level[0]--;
    qnode = wkpathfind5[level[0]];
    store1[0] += 2;
    stackarc[store1[0]-1] = mark[qnode];
    stackarc[store1[0]] = mark[tnode];
  }
  else
    qnode = wkpathfind5[level[0]];
  while (second[qnode] > 0) {
    tnode = first[second[qnode]];
    second[qnode] = second[second[qnode]];
    if (mark[tnode] == 0) {
      snum[0]++;
      mark[tnode] = snum[0];
      level[0]++;
      wkpathfind5[level[0]] = tnode;
      middle[0] = false;
      return;
    }
    store1[0] += 2;
    stackarc[store1[0]-1] = mark[qnode];
    stackarc[store1[0]] = mark[tnode];
  }
  middle[0] = true;
}


static private void planarityDecompose(int n, int m, int n2, int m22, int m33,
       int nm2, int nmp2, int m7n5, int level[], boolean middle[],
       int initp[], int snode[], int pnum[], int nexte[], int store2[], 
       int store3[], int store4[], int store5[],  int trail[],
       int descendant[], int nodebegin[], int wkpathfind5[], int start[],
       int finish[], int first[], int second[], int wkpathfind1[],
       int wkpathfind2[], int wkpathfind3[], int wkpathfind4[], int nextarc[],
       int arctop[], boolean arctype[])
{
  /* this method is used internally by planarityTesting */

  int node1, node2, qnode=0, qnode2, tnode=0, tnode2;
  boolean ind, skip;

  skip = false;
  if (middle[0]) skip = true;
  if (!skip) qnode = wkpathfind5[level[0]];
  while ((second[qnode] != 0) || skip) {
    if (!skip) {
      tnode = first[second[qnode]];
      second[qnode] = second[second[qnode]];
      if (initp[0] == 0) initp[0] = qnode;
    }
    if ((tnode > qnode) || skip) {
      if (!skip) {
        descendant[tnode] = snode[0];
        trail[tnode] = pnum[0];
        level[0]++;
        wkpathfind5[level[0]] = tnode;
        middle[0] = false;
        return;
      }
      skip = false;
      tnode = wkpathfind5[level[0]];
      level[0]--;
      qnode = wkpathfind5[level[0]];
      snode[0] = tnode - 1;
      initp[0] = 0;
      while (qnode <= wkpathfind2[store3[0] + 2])
        store3[0] -= 2;
      while (qnode <= wkpathfind1[store2[0] + 2])
        store2[0] -= 2;
      while (qnode <= wkpathfind3[store4[0] + 3])
        store4[0] -= 3;
      while (qnode <= wkpathfind4[store5[0] + 3])
         store5[0] -= 3;
      ind = false;
      qnode2 = qnode + qnode;
      while ((nodebegin[qnode2 - 1] > wkpathfind3[store4[0] + 2])  &&
             (qnode < wkpathfind3[store4[0] + 2]) &&
             (nodebegin[qnode2] < wkpathfind3[store4[0] + 1])) {
        ind = true;
        node1 = nodebegin[qnode2];
        node2 = wkpathfind3[store4[0] + 1];
        nexte[0]++;
        nextarc[nexte[0]] = nextarc[node1];
        nextarc[node1] = nexte[0];
        arctop[nexte[0]] = node2;
        node1 = wkpathfind3[store4[0] + 1];
        node2 = nodebegin[qnode2];
        nexte[0]++;
        nextarc[nexte[0]] = nextarc[node1];
        nextarc[node1] = nexte[0];
        arctop[nexte[0]] = node2;
        arctype[nexte[0] - 1] = false;
        arctype[nexte[0]] = false;
        store4[0] -= 3;
      }
      if (ind) store4[0] += 3;
      nodebegin[qnode2 - 1] = 0;
      nodebegin[qnode2] = 0;
    }
    else {
      start[pnum[0] + 1] = initp[0];
      finish[pnum[0] + 1] = tnode;
      ind = false;
      if (wkpathfind1[store2[0]+2] != 0) {
        store3[0] += 2;
        wkpathfind2[store3[0]+1] = wkpathfind1[store2[0]+1];
        wkpathfind2[store3[0]+2] = wkpathfind1[store2[0]+2];
      }
      if (finish[wkpathfind1[store2[0]+1] + 1] != tnode) {
        while (tnode < wkpathfind2[store3[0]+2]) {
          node1 = pnum[0];
          node2 = wkpathfind2[store3[0] + 1];
          nexte[0]++;
          nextarc[nexte[0]] = nextarc[node1];
          nextarc[node1] = nexte[0];
          arctop[nexte[0]] = node2;
          node1 = wkpathfind2[store3[0] + 1];
          node2 = pnum[0];
          nexte[0]++;
          nextarc[nexte[0]] = nextarc[node1];
          nextarc[node1] = nexte[0];
          arctop[nexte[0]] = node2;
          arctype[nexte[0] - 1] = true;
          arctype[nexte[0]] = true;
          ind  = true;
          store3[0] -= 2;
        }
        if (ind) store3[0] += 2;
        ind = false;
        while ((tnode < wkpathfind3[store4[0]+3]) &&
               (initp[0] < wkpathfind3[store4[0]+2])) {
          node1 = pnum[0];
          node2 = wkpathfind3[store4[0] + 1];
          nexte[0]++;
          nextarc[nexte[0]] = nextarc[node1];
          nextarc[node1] = nexte[0];
          arctop[nexte[0]] = node2;
          node1 = wkpathfind3[store4[0] + 1];
          node2 = pnum[0];
          nexte[0]++;
          nextarc[nexte[0]] = nextarc[node1];
          nextarc[node1] = nexte[0];
          arctop[nexte[0]] = node2;
          arctype[nexte[0] - 1] = false;
          arctype[nexte[0]] = false;
          store4[0] -= 3;
        }
        while ((tnode < wkpathfind4[store5[0]+3])  &&
               (initp[0] < wkpathfind4[store5[0]+2]))
          store5[0] -= 3;
        tnode2 = tnode + tnode;
        if (initp[0] > nodebegin[tnode2-1]) {
          nodebegin[tnode2-1] = initp[0];
          nodebegin[tnode2] = pnum[0];
        }
        store4[0] += 3;
        wkpathfind3[store4[0]+1] = pnum[0];
        wkpathfind3[store4[0]+2] = initp[0];
        wkpathfind3[store4[0]+3] = tnode;
        store5[0] += 3;
        wkpathfind4[store5[0]+1] = pnum[0];
        wkpathfind4[store5[0]+2] = initp[0];
        wkpathfind4[store5[0]+3] = tnode;
      }
      else {
        while ((tnode < wkpathfind4[store5[0]+3])  &&
               (initp[0] < wkpathfind4[store5[0]+2])  &&
               (wkpathfind4[store5[0]+2] <= descendant[initp[0]])) {
          ind = true;
          node1 = pnum[0];
          node2 = wkpathfind4[store5[0] + 1];
          nexte[0]++;
          nextarc[nexte[0]] = nextarc[node1];
          nextarc[node1] = nexte[0];
          arctop[nexte[0]] = node2;
          node1 = wkpathfind4[store5[0] + 1];
          node2 = pnum[0];
          nexte[0]++;
          nextarc[nexte[0]] = nextarc[node1];
          nextarc[node1] = nexte[0];
          arctop[nexte[0]] = node2;
          arctype[nexte[0] - 1] = false;
          arctype[nexte[0]] = false;
          store5[0] -= 3;
        }
        if (ind) store5[0] += 3;
      }
      if (qnode != initp[0]) {
        store2[0] += 2;
        wkpathfind1[store2[0]+1] = pnum[0];
        wkpathfind1[store2[0]+2] = initp[0];
      }
      pnum[0]++;
      initp[0] = 0;
    }
  }
  middle[0] = true;
}


static private void planarityTwoColoring(int m, int nmp2, int m7n5, int level[],
               boolean middle[], boolean fail[], int wkpathfind5[], int paint[],
               int nextarc[], int arctop[], boolean examin[], boolean arctype[])
{
  /* this method is used internally by planarityTesting */

  int link, qnode, tnode;
  boolean dum1, dum2;

  fail[0] = false;
  if (middle[0]) {
    level[0]--;
    qnode = wkpathfind5[level[0]];
  }
  else
    qnode = wkpathfind5[level[0]];
  while (nextarc[qnode] != 0) {
    link  = nextarc[qnode];
    tnode = arctop[link];
    nextarc[qnode] = nextarc[link];
    if (paint[tnode] == 0)
      paint[tnode] = (arctype[link] ? paint[qnode] : 3 - paint[qnode]);
    else {
      dum1 = (paint[tnode] == paint[qnode]);
      dum2 = !arctype[link];
      if ((dum1 && dum2) || (!dum1 && !dum2)) {
        fail[0] = true;
        return;
      }
    }
    if (examin[tnode]) {
      examin[tnode] = false;
      level[0]++;
      wkpathfind5[level[0]] = tnode;
      middle[0] = false;
      return;
    }
  }
  middle[0] = true;
}


static private void qapsubprog1(int n, int aux15[], int aux12[],
               int large, int parm1[], int parm2[], int parm3[])
{
  /* this method is used internally by quadraticAssignment */

  // compute the alternative costs and obtain the assignment

  int i,j,leastw,leastx,p,q,valc,valp;

  parm3[0] = -1;
  valc = 0;
  for (i=1; i<=n; i++) {
    j = aux12[i];
    valp = j - n;
    leastw = large;
    for (p=1; p<=n; p++) {
      valp += n;
      if (p != i) {
        q = aux15[valp];
        if (q < leastw) leastw = q;
      }
    }
    leastx = leastw;
    leastw = large;
    for (p=1; p<=n; p++) {
      valc++;
      if (p != j) {
        q = aux15[valc];
        if (q < leastw) leastw = q;
      }
    }
    leastw += leastx;
    if (leastw > parm3[0]) {
      parm1[0] = i;
      parm2[0] = j;
      parm3[0] = leastw;
    }
  }
}


static private void qapsubprog2(int n, int k, int npp, int subcripta,
                     int subcriptb, int a[][], int b[][], int aux4[],
                     int vekt[], boolean work1[], boolean work2[],
                     int aux16[], int aux17[], int aux13[])
{
  /* this method is used internally by quadraticAssignment */

  // obtain rows of the matrix a in decreasing order
  // and rows of matrix b in increasing order

  int i,j,nppa,nppb,nppc,valg,valp,valq,valx;
  boolean decide;

  nppa = npp - 1;
  if (npp == n) {
    valp = 1;
    for (i=1; i<=n; i++) {
      valx = 0;
      for (j=1; j<=n; j++)
        if (j != i) {
          valx++;
          vekt[valx] = a[i][j];
        }
      qapsubprog5(vekt,aux13,nppa);
      for (j=1; j<=nppa; j++) {
        nppb = npp - j;
        aux16[valp] = vekt[nppb];
        valp++;
      }
    }
    valp = 1;
    for (i=1; i<=n; i++) {
      valx = 0;
      for (j=1; j<=n; j++)
        if (j != i) {
          valx++;
          vekt[valx] = b[i][j];
        }
      qapsubprog5(vekt,aux13,nppa);
      for (j=1; j<=nppa; j++) {
        aux17[valp] = vekt[j];
        valp++;
      }
    }
    return;
  }
  nppc = aux4[k+1];
  valp = nppc;
  valq = nppc - (npp + 1) * npp;
  for (i=1; i<=n; i++)
    if (work1[i]) {
      if (i == subcripta) valq += npp;
    }
    else {
      valg = a[i][subcripta];
      decide = true;
      for (j=1; j<=npp; j++) {
        valq++;
        if ((aux16[valq] == valg) && decide)
          decide = false;
        else {
          valp++;
          aux16[valp] = aux16[valq];
        }
      }
    }
  valp = nppc;
  valq = nppc - (npp + 1) * npp;
  for (i=1; i<=n; i++) {
    if (work2[i]) {
      if (i == subcriptb) valq += npp;
    }
    else {
      valg = b[i][subcriptb];
      decide = true;
      for (j=1; j<=npp; j++) {
        valq++;
        if ((aux17[valq] == valg) && decide)
          decide = false;
        else {
          valp++;
          aux17[valp] = aux17[valq];
        }
      }
    }
  }
  return;
}


static private void qapsubprog3 (int n, int k, int npp, int aux15[],
                 int aux4[], int aux5[], boolean contr, int aux16[],
                 int aux17[], int aux18[])
{
  /* this method is used internally by quadraticAssignment */

  // compute the cost matrix of the k-th linear assignment problem

  int accum,i,j,idx,nppd,valg,valp,valq,valr,vals,valt,valx;

  idx = 0;
  if (npp == n) {
    accum = 0;
    valp = 0;
    if (contr) {
      valq = accum;
      nppd = npp - 1;
      for (i=1; i<=npp; i++) {
        valt = accum;
        for (j=1; j<=npp; j++) {
          valp++;
          idx++;
          valg = 0;
          for (valx=1; valx<=nppd; valx++) {
            valr = valq + valx;
            vals = valt + valx;
            valg += aux16[valr] * aux17[vals];
          }
          valt += nppd;
          aux18[valp] = valg;
          aux15[idx] = valg;
        }
        valq += nppd;
      }
      return;
    }
    for (i=1; i<=npp; i++)
      for (j=1; j<=npp; j++) {
        valp++;
        idx++;
        aux15[idx] = aux18[valp];
      }
    return;
  }
  if (!contr) {
    valp = aux5[k];
    for (i=1; i<=npp; i++)
      for (j=1; j<=npp; j++) {
        valp++;
        idx++;
        aux15[idx] = aux18[valp];
      }
    return;
  }
  accum = aux4[k+1];
  valp = aux5[k+1];
  valq = accum;
  nppd = npp - 1;
  for (i=1; i<=npp; i++) {
    valt = accum;
    for (j=1; j<=npp; j++) {
      valp++;
      idx++;
      valg = 0;
      for (valx=1; valx<=nppd; valx++) {
        valr = valq + valx;
        vals = valt + valx;
        valg += aux16[valr] * aux17[vals];
      }
      valt += nppd;
      aux18[valp] = valg;
      aux15[idx] = valg;
    }
    valq += nppd;
  }
  return;
}


static private void qapsubprog4(int n, int large, int c[], int parm[],
                int wk1[], int wk2[], int wk3[], int wk4[], int wk5[],
                int wk6[], int wk7[], boolean wk8[])
{
  /* this method is used internally by quadraticAssignment */

  // solve linear sum assignment problem

  int i,j,p,v1,v2,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15;
  int v3=0,v4=0,v16=0;

  // initial assignment
  for (i=1; i<=n; i++) {
    wk1[i] = 0;
    wk2[i] = 0;
    wk5[i] = 0;
    wk6[i] = 0;
    wk7[i] = 0;
  }
  v1=0;
  for (i=1; i<=n; i++) {
    for (j=1; j<=n; j++) {
      v1++;
      v2 = c[v1];
      if (j == 1) {
        v3 = v2;
        v4 = j;
      }
      else {
        if ((v2 - v3) < 0) {
          v3 = v2;
          v4 = j;
        }
      }
    }
    wk5[i] = v3;
    if (wk1[v4] == 0) {
      wk1[v4] = i;
      wk2[i] = v4;
    }
  }
  for (j=1; j<=n; j++) {
    wk6[j] = 0;
    if (wk1[j] == 0) wk6[j] = large;
  }
  v1 = 0;
  for (i=1; i<=n; i++) {
    v3 = wk5[i];
    for (j=1; j<=n; j++) {
      v1++;
      v15 = wk6[j];
      if (v15 > 0) {
        v2 = c[v1] - v3;
        if (v2 < v15) {
          wk6[j] = v2;
          wk7[j] = i;
        }
      }
    }
  }
  for (j=1; j<=n; j++) {
    i = wk7[j];
    if (i != 0) {
      if (wk2[i] == 0) {
        wk2[i] = j;
        wk1[j] = i;
      }
    }
  }
  for (i=1; i<=n; i++)
    if (wk2[i] == 0) {
      v3 = wk5[i];
      v1 = (i - 1) * n;
      for (j=1; j<=n; j++) {
        v1++;
        if (wk1[j] == 0) {
          v2 = c[v1];
          if ((v2 - v3 - wk6[j]) <= 0) {
            wk2[i] = j;
            wk1[j] = i;
            break;
          }
        }
      }
    }
  // construct the optimal assignment
  for (p=1; p<=n; p++)
    if (wk2[p] <= 0) {
      // compute shortest path
      v5 = (p - 1) * n;
      for (i=1; i<=n; i++) {
        wk7[i] = p;
        wk8[i] = false;
        wk4[i] = large;
        v6 = v5 + i;
        wk3[i] = c[v6] - wk5[p] - wk6[i];
      }
      wk4[p] = 0;
      while (true) {
        v14 = large;
        for (i=1; i<=n; i++)
          if (!wk8[i]) {
            if (wk3[i] < v14) {
              v14 = wk3[i];
              v16 = i;
            }
          }
        if (wk1[v16] <= 0) break;
        wk8[v16] = true;
        v7 = wk1[v16];
        v8 = (v7 - 1) * n;
        wk4[v7] = v14;
        for (i=1; i<=n; i++)
          if (!wk8[i]) {
            v9 = v8 + i;
            v10 = v14 + c[v9] - wk5[v7] - wk6[i];
            if (wk3[i] > v10) {
              wk3[i] = v10;
              wk7[i] = v7;
            }
          }
      }
      // augmentation
      while (true) {
        v7 = wk7[v16];
        wk1[v16] = v7;
        v11 = wk2[v7];
        wk2[v7] = v16;
        if (v7 == p) break;
        v16 = v11;
      }
      // transformation
      for (i=1; i<=n; i++) {
        if (wk4[i] != large)
          wk5[i] += v14 - wk4[i];
        if (wk3[i] < v14)
          wk6[i] += wk3[i] - v14;
      }
    }
  // compute the optimal value
  parm[0] = 0;
  for (i=1; i<=n; i++) {
    v12 = (i - 1) * n;
    j = wk2[i];
    v13 = v12 + j;
    parm[0] += c[v13];
  }
}


static private void qapsubprog5 (int a[], int b[], int dim)
{
  /* this method is used internally by quadraticAssignment */

  // sort the vector a in increasing order
  // b is the permutation vector of the sorted vector

  int i,j,ina,inb,inx,iny,low,half,high,p,quant;

  low = 1;
  if (dim <= low) return;
  half = (dim - low + 1) / 2;
  quant = 1023;
  for (p=1; p<=10; p++) {
    if (quant <= half) {
      high = dim - quant;
      for (i=low; i<=high; i++) {
        inx = i + quant;
        ina = a[inx];
        inb = b[inx];
        j = i;
        iny = inx;
        while (ina < a[j]) {
          a[iny] = a[j];
          b[iny] = b[j];
          iny = j;
          j -= quant;
          if (j < low) break;
        }
        a[iny] = ina;
        b[iny] = inb;
      }
    }
    quant /= 2;
  }
}


static private void tspsearch(int nodes, int edges, int weight, 
                              int dist[][], int row[], int column[],
                              int cursol[], int front[], int back[])
{
  /* this method is used internally by travelingSalesmanProblem*/

  int i, j, k, reduction, small, skip, stretch, candc=0, candr=0;
  int elms, head, tail, thresh, diff, miny, minx, blank;
  int cutx[] = new int[nodes + 1];
  int cuty[] = new int[nodes + 1];
  int rowvec[] = new int[nodes + 1];
  int colvec[] = new int[nodes + 1];

  elms = nodes - edges;
  reduction = 0;
  for (i=1; i<=elms ; i++) {
    small  = Integer.MAX_VALUE;
    for (j=1; j<=elms ; j++)
      small = Math.min(small, dist[row[i]][column[j]]);
    if (small > 0) {
      for (j=1; j<=elms ; j++)
        if (dist[row[i]][column[j]] < Integer.MAX_VALUE) 
          dist[row[i]][column[j]] -= small;
      reduction += small;
    }
    cutx[i] = small;
  }
  for (j=1; j<=elms ; j++) {
    small = Integer.MAX_VALUE;
    for (i=1; i<=elms ; i++) 
      small = Math.min(small, dist[row[i]][column[j]]);
    if (small > 0) {
      for (i=1; i<=elms ; i++) 
        if (dist[row[i]][column[j]] < Integer.MAX_VALUE)
          dist[row[i]][column[j]] -= small;
      reduction += small;
    }
    cuty[j] = small;
  }      
  weight += reduction;
  if (weight < dist[0][0]) {
    if (edges == (nodes - 2)) {
      for (i=1; i<=nodes; i++)
        cursol[i] = front[i];
      skip = (dist[row[1]][column[1]] == Integer.MAX_VALUE ? 1 : 2);
      cursol[row[1]] = column[3-skip];
      cursol[row[2]] = column[skip];
      dist[0][0] = weight;
    }
    else {
      diff = -Integer.MAX_VALUE;
      for (i=1; i<=elms; i++)
        for (j=1; j<=elms; j++)
          if (dist[row[i]][column[j]] == 0) {
            minx = Integer.MAX_VALUE;  
            blank = 0;
            for (k=1; k<=elms; k++)
              if (dist[row[i]][column[k]] == 0) 
                blank++;
              else
                minx = Math.min(minx, dist[row[i]][column[k]]);
            if (blank > 1) minx = 0;
            miny = Integer.MAX_VALUE;
            blank = 0;
            for (k=1; k<=elms; k++)
              if (dist[row[k]][column[j]] == 0)
                blank++;
              else 
                miny = Math.min(miny, dist[row[k]][column[j]]);
            if (blank > 1) miny = 0;
            if ((minx + miny) > diff) {
              diff = minx + miny;
              candr = i;
              candc = j;
            }
          }
      thresh = weight + diff;
      front[row[candr]] = column[candc];
      back[column[candc]] = row[candr];
      tail = column[candc];
      while (front[tail] != 0) 
        tail = front[tail];
      head = row[candr];
      while (back[head] != 0) 
        head = back[head];
      stretch = dist[tail][head];
      dist[tail][head] = Integer.MAX_VALUE;
      for (i=1; i<=candr-1; i++)
        rowvec[i] = row[i];
      for (i=candr; i<=elms-1; i++)
        rowvec[i] = row[i+1];
      for (i=1; i<=candc-1; i++)
        colvec[i] = column[i];
      for (i=candc; i<=elms-1; i++)
        colvec[i] = column[i+1];
      tspsearch(nodes,edges+1,weight,dist,rowvec,colvec,cursol,front,back);
      dist[tail][head] = stretch;
      back[column[candc]] = 0;
      front[row[candr]] = 0;
      if (thresh < dist[0][0]) {
        dist[row[candr]][column[candc]] = Integer.MAX_VALUE;
        tspsearch(nodes,edges,weight,dist,row,column,cursol,front,back);
        dist[row[candr]][column[candc]] = 0;
      }
    }
  }
  for (i=1; i<=elms; i++)
    for (j=1; j<=elms; j++)
      dist[row[i]][column[j]] += (cutx[i] + cuty[j]);
}

}