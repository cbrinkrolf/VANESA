package graph.algorithms;

import java.util.Random;

public class GraphTheoryAlgorithms extends Object {

	public static void allShortestPathLength(int n, int m, int nodei[], int nodej[], boolean directed[], int weight[],
			int root, int mindistance[]) {
		int i, j, k, n2, large, nodeu, nodev, minlen, temp, minj = 0, minv = 0;
		int location[] = new int[n + 1];
		int distance[][] = new int[n + 1][n + 1];

		// obtain a large number greater than all edge weights
		large = 1;
		for (k = 1; k <= m; k++)
			large += weight[k];

		// set up the distance matrix
		for (i = 1; i <= n; i++)
			for (j = 1; j <= n; j++)
				distance[i][j] = (i == j) ? 0 : large;
		for (k = 1; k <= m; k++) {
			i = nodei[k];
			j = nodej[k];
			if (directed[k])
				distance[i][j] = weight[k];
			else
				distance[i][j] = distance[j][i] = weight[k];
		}

		if (root != 1) {
			// interchange rows 1 and root
			for (i = 1; i <= n; i++) {
				temp = distance[1][i];
				distance[1][i] = distance[root][i];
				distance[root][i] = temp;
			}
			// interchange columns 1 and root
			for (i = 1; i <= n; i++) {
				temp = distance[i][1];
				distance[i][1] = distance[i][root];
				distance[i][root] = temp;
			}
		}
		nodeu = 1;
		n2 = n + 2;
		for (i = 1; i <= n; i++) {
			location[i] = i;
			mindistance[i] = distance[nodeu][i];
		}
		for (i = 2; i <= n; i++) {
			k = n2 - i;
			minlen = large;
			for (j = 2; j <= k; j++) {
				nodev = location[j];
				temp = mindistance[nodeu] + distance[nodeu][nodev];
				if (temp < mindistance[nodev])
					mindistance[nodev] = temp;
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
			for (i = 1; i <= n; i++) {
				temp = distance[1][i];
				distance[1][i] = distance[root][i];
				distance[root][i] = temp;
			}
			// interchange columns 1 and root
			for (i = 1; i <= n; i++) {
				temp = distance[i][1];
				distance[i][1] = distance[i][root];
				distance[i][root] = temp;
			}
		}
	}

	public static boolean connected(int n, int m, int nodei[], int nodej[]) {
		int i, j, k, r, connect;
		int neighbor[] = new int[m + m + 1];
		int degree[] = new int[n + 1];
		int index[] = new int[n + 2];
		int aux1[] = new int[n + 1];
		int aux2[] = new int[n + 1];

		for (i = 1; i <= n; i++)
			degree[i] = 0;
		for (j = 1; j <= m; j++) {
			degree[nodei[j]]++;
			degree[nodej[j]]++;
		}
		index[1] = 1;
		for (i = 1; i <= n; i++) {
			index[i + 1] = index[i] + degree[i];
			degree[i] = 0;
		}
		for (j = 1; j <= m; j++) {
			neighbor[index[nodei[j]] + degree[nodei[j]]] = nodej[j];
			degree[nodei[j]]++;
			neighbor[index[nodej[j]] + degree[nodej[j]]] = nodei[j];
			degree[nodej[j]]++;
		}
		for (i = 2; i <= n; i++)
			aux1[i] = 1;
		aux1[1] = 0;
		connect = 1;
		aux2[1] = 1;
		k = 1;
		while (true) {
			i = aux2[k];
			k--;
			for (j = index[i]; j <= index[i + 1] - 1; j++) {
				r = neighbor[j];
				if (aux1[r] != 0) {
					connect++;
					if (connect == n) {
						connect /= n;
						if (connect == 1)
							return true;
						return false;
					}
					aux1[r] = 0;
					k++;
					aux2[k] = r;
				}
			}
			if (k == 0) {
				connect /= n;
				if (connect == 1)
					return true;
				return false;
			}
		}
	}

	public static void connectedComponents(int n, int m, int nodei[], int nodej[], int component[]) {
		int edges, i, j, numcomp, p, q, r, typea, typeb, typec, tracka, trackb;
		int compkey, key1, key2, key3, nodeu, nodev;
		int numnodes[] = new int[n + 1];
		int aux[] = new int[n + 1];
		int index[] = new int[3];

		typec = 0;
		index[1] = 1;
		index[2] = 2;
		q = 2;
		for (i = 1; i <= n; i++) {
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
			if (key1 < 0)
				key1 = nodeu;
			key2 = component[nodev];
			if (key2 < 0)
				key2 = nodev;
			if (key1 == key2) {
				if (j >= edges) {
					edges--;
					break;
				}
				nodei[j] = nodei[edges];
				nodej[j] = nodej[edges];
				nodei[edges] = nodeu;
				nodej[edges] = nodev;
				edges--;
			} else {
				if (numnodes[key1] >= numnodes[key2]) {
					key3 = key1;
					key1 = key2;
					key2 = key3;
					typec = -component[key2];
				} else
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
				if (j > edges || j > n)
					break;
			}
		} while (true);
		numcomp = 0;
		for (i = 1; i <= n; i++)
			if (numnodes[i] != 0) {
				numcomp++;
				numnodes[numcomp] = numnodes[i];
				aux[i] = numcomp;
			}
		for (i = 1; i <= n; i++) {
			key3 = component[i];
			if (key3 < 0)
				key3 = i;
			component[i] = aux[key3];
		}
		if (numcomp == 1) {
			component[0] = numcomp;
			return;
		}
		typeb = numnodes[1];
		numnodes[1] = 1;
		for (i = 2; i <= numcomp; i++) {
			typea = numnodes[i];
			numnodes[i] = numnodes[i - 1] + typeb - 1;
			typeb = typea;
		}
		for (i = 1; i <= edges; i++) {
			typec = nodei[i];
			compkey = component[typec];
			aux[i] = numnodes[compkey];
			numnodes[compkey]++;
		}
		for (i = 1; i <= q; i++) {
			typea = index[i];
			do {
				if (typea <= i)
					break;
				typeb = index[typea];
				index[typea] = -typeb;
				typea = typeb;
			} while (true);
			index[i] = -index[i];
		}
		if (aux[1] >= 0)
			for (j = 1; j <= edges; j++) {
				tracka = aux[j];
				do {
					if (tracka <= j)
						break;
					trackb = aux[tracka];
					aux[tracka] = -trackb;
					tracka = trackb;
				} while (true);
				aux[j] = -aux[j];
			}
		for (i = 1; i <= q; i++) {
			typea = -index[i];
			if (typea >= 0) {
				r = 0;
				do {
					typea = index[typea];
					r++;
				} while (typea > 0);
				typea = i;
				for (j = 1; j <= edges; j++)
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
									if (typea == i)
										break;
								}
							} while (true);
							trackb = Math.abs(aux[trackb]);
						} while (p != 0);
					}
			}
		}
		for (i = 1; i <= q; i++)
			index[i] = Math.abs(index[i]);
		if (aux[1] > 0) {
			component[0] = numcomp;
			return;
		}
		for (j = 1; j <= edges; j++)
			aux[j] = Math.abs(aux[j]);
		typea = 1;
		for (i = 1; i <= numcomp; i++) {
			typeb = numnodes[i];
			numnodes[i] = typeb - typea + 1;
			typea = typeb;
		}
		component[0] = numcomp;
	}

	public static int cutNodes(int n, int m, int nodei[], int nodej[], int cutnode[]) {
		int i, j, k, nodeu, nodev, node1, node2, node3, node4, numblocks;
		int root, p, edges, index, len1, len2, low, up, components;
		int totalcutnodes, numcutnodes = 0;
		int firstedges[] = new int[n + 1];
		int label[] = new int[n + 1];
		int nextnode[] = new int[n + 1];
		int length[] = new int[n + 1];
		int cutvertex[] = new int[n + 1];
		int cutedge[] = new int[m + 1];
		boolean mark[] = new boolean[n + 1];
		boolean join;

		totalcutnodes = 0;
		for (i = 1; i <= n; i++)
			nextnode[i] = 0;
		components = 0;
		for (root = 1; root <= n; root++) {
			if (nextnode[root] == 0) {
				components++;
				// set up the forward star representation of the graph
				k = 0;
				for (i = 1; i <= n - 1; i++) {
					firstedges[i] = k + 1;
					for (j = 1; j <= m; j++) {
						nodeu = nodei[j];
						nodev = nodej[j];
						if ((nodeu == i) && (nodeu < nodev)) {
							k++;
							cutedge[k] = nodev;
						} else {
							if ((nodev == i) && (nodev < nodeu)) {
								k++;
								cutedge[k] = nodeu;
							}
						}
					}
				}
				firstedges[n] = m + 1;
				for (i = 1; i <= n; i++) {
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
					for (node2 = 1; node2 <= n; node2++) {
						join = false;
						if (node2 != node3) {
							if (node2 < node3) {
								nodeu = node2;
								nodev = node3;
							} else {
								nodeu = node3;
								nodev = node2;
							}
							low = firstedges[nodeu];
							up = firstedges[nodeu + 1];
							if (up > low) {
								up--;
								for (k = low; k <= up; k++)
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
							} else {
								if (node1 < 0) {
									// next block
									node4 = label[node2];
									if (node4 > 0)
										mark[node4] = true;
									label[node2] = node3;
									len2 = length[node3] - length[-node1];
									if (len2 > len1)
										len1 = len2;
								}
							}
						}
					}
					if (len1 > 0) {
						j = node3;
						while (true) {
							len1--;
							if (len1 < 0)
								break;
							p = label[j];
							if (p > 0)
								mark[p] = true;
							label[j] = node3;
							j = nextnode[j];
						}
						for (i = 1; i <= n; i++) {
							p = label[i];
							if (p > 0)
								if (mark[p])
									label[i] = node3;
						}
					}
					edges++;
				} while ((edges <= n) && (index > 0));
				nextnode[root] = 0;
				node3 = cutvertex[1];
				nextnode[node3] = Math.abs(nextnode[node3]);
				numblocks = 0;
				numcutnodes = 0;
				for (i = 1; i <= n; i++)
					if (i != root) {
						node3 = label[i];
						if (node3 < 0) {
							numblocks++;
							label[i] = n + numblocks;
						} else {
							if ((node3 <= n) && (node3 > 0)) {
								numblocks++;
								node4 = n + numblocks;
								for (j = i; j <= n; j++)
									if (label[j] == node3)
										label[j] = node4;
							}
						}
					}
				for (i = 1; i <= n; i++) {
					p = label[i];
					if (p > 0)
						label[i] = p - n;
				}
				i = 1;
				while (nextnode[i] != root)
					i++;
				label[root] = label[i];
				for (i = 1; i <= n; i++) {
					node1 = nextnode[i];
					if (node1 > 0) {
						p = Math.abs(label[node1]);
						if (Math.abs(label[i]) != p)
							label[node1] = -p;
					}
				}
				for (i = 1; i <= n; i++)
					if (label[i] < 0)
						numcutnodes++;
				// store the cut nodes
				j = 0;
				for (i = 1; i <= n; i++)
					if (label[i] < 0) {
						j++;
						cutvertex[j] = i;
					}
				// find the end-nodes
				for (i = 1; i <= n; i++)
					length[i] = 0;
				for (i = 1; i <= m; i++) {
					j = nodei[i];
					length[j]++;
					j = nodej[i];
					length[j]++;
				}
				for (i = 1; i <= n; i++)
					if (length[i] == 1)
						if (label[i] > 0)
							label[i] = -label[i];
				for (p = 1; p <= numcutnodes; p++) {
					totalcutnodes++;
					cutnode[totalcutnodes] = cutvertex[p];
				}
			}
		}
		cutnode[0] = totalcutnodes;
		return components;
	}

	public static int edgeConnectivity(int n, int m, int nodei[], int nodej[]) {
		int i, j, k, m2, source, sink;
		int minimumcut[] = new int[n + 1];
		int edgei[] = new int[4 * m + 1];
		int edgej[] = new int[4 * m + 1];
		int capac[] = new int[4 * m + 1];
		int arcflow[] = new int[4 * m + 1];
		int nodeflow[] = new int[4 * m + 1];

		k = n;
		source = 1;
		m2 = m + m;
		for (sink = 2; sink <= n; sink++) {
			// construct the network
			for (i = 1; i <= 4 * m; i++) {
				edgei[i] = 0;
				edgej[i] = 0;
				capac[i] = 0;
			}
			// duplicate the edges
			j = 0;
			for (i = 1; i <= m; i++) {
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
			maximumNetworkFlow(n, m2, edgei, edgej, capac, source, sink, minimumcut, arcflow, nodeflow);
			if (nodeflow[source] < k)
				k = nodeflow[source];
		}
		return k;
	}

	public static void fundamentalCycles(int n, int m, int nodei[], int nodej[], int fundcycle[][]) {
		int i, j, k, nodeu, nodev, components, numcycles, root, index, edges;
		int low, len, up, node1, node2, node3;
		int endnode[] = new int[m + 1];
		int firstedges[] = new int[n + 1];
		int nextnode[] = new int[n + 1];
		int pointer[] = new int[n + 1];
		int currentcycle[] = new int[n + 1];
		boolean join;

		// set up the forward star representation of the graph
		k = 0;
		for (i = 1; i <= n - 1; i++) {
			firstedges[i] = k + 1;
			for (j = 1; j <= m; j++) {
				nodeu = nodei[j];
				nodev = nodej[j];
				if ((nodeu == i) && (nodeu < nodev)) {
					k++;
					endnode[k] = nodev;
				} else {
					if ((nodev == i) && (nodev < nodeu)) {
						k++;
						endnode[k] = nodeu;
					}
				}
			}
		}
		firstedges[n] = m + 1;
		for (i = 1; i <= n; i++)
			nextnode[i] = 0;
		components = 0;
		numcycles = 0;
		for (root = 1; root <= n; root++)
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
					for (node2 = 1; node2 <= n; node2++) {
						join = false;
						if (node2 != node3) {
							if (node2 < node3) {
								nodeu = node2;
								nodev = node3;
							} else {
								nodeu = node3;
								nodev = node2;
							}
							low = firstedges[nodeu];
							up = firstedges[nodeu + 1];
							if (up > low) {
								up--;
								for (k = low; k <= up; k++)
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
							} else {
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
										if (j == node1)
											break;
										len++;
										currentcycle[len] = j;
										i = j;
									}
									// store the current fundamental cycle
									fundcycle[numcycles][0] = len;
									for (i = 1; i <= len; i++)
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

	public static void maximumNetworkFlow(int n, int m, int nodei[], int nodej[], int capacity[], int source, int sink,
			int minimumcut[], int arcflow[], int nodeflow[]) {
		int i, j, curflow, flag, medge, nodew;
		int in = 0, iout = 0, parm = 0, m1 = 0, icont = 0, jcont = 0;
		int last = 0, nodep = 0, nodeq = 0, nodeu = 0, nodev = 0, nodex = 0, nodey = 0;
		int firstarc[] = new int[n + 1];
		int imap[] = new int[n + 1];
		int jmap[] = new int[n + 1];
		boolean finish, controla, controlb, controlc, controlg;
		boolean controld = false, controle = false, controlf = false;

		// create the artificial edges
		j = m;
		for (i = 1; i <= m; i++) {
			j++;
			nodei[m + i] = nodej[i];
			nodej[m + i] = nodei[i];
			capacity[m + i] = 0;
		}
		m = m + m;
		// initialize
		for (i = 1; i <= n; i++)
			firstarc[i] = 0;
		curflow = 0;
		for (i = 1; i <= m; i++) {
			arcflow[i] = 0;
			j = nodei[i];
			if (j == source)
				curflow += capacity[i];
			firstarc[j]++;
		}
		nodeflow[source] = curflow;
		nodew = 1;
		for (i = 1; i <= n; i++) {
			j = firstarc[i];
			firstarc[i] = nodew;
			imap[i] = nodew;
			nodew += j;
		}
		finish = false;
		controla = true;
		// sort the edges in lexicographical order
		entry1: while (true) {
			flag = 0;
			controlb = false;
			entry2: while (true) {
				if (!controlb) {
					if ((flag < 0) && controla) {
						if (flag != -1) {
							if (nodew < 0)
								nodep++;
							nodeq = jcont;
							jcont = nodep;
							flag = -1;
						} else {
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
							} else
								flag = 2;
						}
					} else {
						if (controla)
							if (flag > 0) {
								if (flag <= 1)
									jcont = icont;
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
						} else {
							if (nodep == m1) {
								nodeq = jcont;
								jcont = nodep;
								flag = -1;
							} else {
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
					if (nodew == 0)
						nodew = nodej[nodep] - nodej[nodeq];
					continue entry2;
				} else {
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
						if (flag > 0)
							continue entry2;
						if (flag == 0) {
							controlc = true;
						} else {
							jmap[nodev] = nodeq;
							controlg = true;
						}
					} else if (finish) {
						// return the maximum flow on each edge
						j = 0;
						for (i = 1; i <= m; i++)
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
					for (i = 1; i <= m; i++) {
						nodev = nodej[i];
						nodei[i] = imap[nodev];
						imap[nodev]++;
					}
				}
				entry3: while (true) {
					if (!controlg) {
						if (!controlc) {
							flag = 0;
							for (i = 1; i <= n; i++) {
								if (i != source)
									nodeflow[i] = 0;
								jmap[i] = m + 1;
								if (i < n)
									jmap[i] = firstarc[i + 1];
								minimumcut[i] = 0;
							}
							in = 0;
							iout = 1;
							imap[1] = source;
							minimumcut[source] = -1;
							while (true) {
								in++;
								if (in > iout)
									break;
								nodeu = imap[in];
								medge = jmap[nodeu] - 1;
								last = firstarc[nodeu] - 1;
								while (true) {
									last++;
									if (last > medge)
										break;
									nodev = nodej[last];
									curflow = capacity[last] - arcflow[last];
									if ((minimumcut[nodev] != 0) || (curflow == 0))
										continue;
									if (nodev != sink) {
										iout++;
										imap[iout] = nodev;
									}
									minimumcut[nodev] = -1;
								}
							}
							if (minimumcut[sink] == 0) {
								// exit
								for (i = 1; i <= n; i++)
									minimumcut[i] = -minimumcut[i];
								for (i = 1; i <= m; i++) {
									nodeu = nodej[nodei[i]];
									if (arcflow[i] < 0)
										nodeflow[nodeu] -= arcflow[i];
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
								if (in == 0)
									break;
								nodeu = imap[in];
								nodep = firstarc[nodeu] - 1;
								nodeq = jmap[nodeu] - 1;
							}
							controlc = false;
							while (nodep != nodeq) {
								nodev = nodej[nodeq];
								if ((minimumcut[nodev] <= 0) || (capacity[nodeq] == arcflow[nodeq])) {
									nodeq--;
									continue;
								} else {
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
							if (nodep >= firstarc[nodeu])
								minimumcut[nodeu] = nodep;
						}
						nodex = 0;
						for (i = 1; i <= iout; i++)
							if (minimumcut[imap[i]] > 0) {
								nodex++;
								imap[nodex] = imap[i];
							}
						// find a feasible flow
						flag = -1;
						nodey = 1;
					}
					entry4: while (true) {
						if (!controlg) {
							if (!controlf) {
								if (!controld && !controle)
									nodeu = imap[nodey];
								if ((nodeflow[nodeu] <= 0) || controld || controle) {
									if (!controle) {
										controld = false;
										nodey++;
										if (nodey <= nodex)
											continue entry4;
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
											if (nodeu < n)
												medge = firstarc[nodeu + 1];
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
												if (nodeflow[nodeu] < curflow)
													curflow = nodeflow[nodeu];
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
									for (i = 1; i <= m; i++) {
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
								// an outgoing edge from a node is given maximum
								// flow
								last = minimumcut[nodeu] + 1;
							}
						}
						while (true) {
							if (!controlg) {
								controlf = false;
								last--;
								if (last < firstarc[nodeu])
									break;
								nodev = -nodej[last];
								if (nodeflow[nodev] < 0)
									continue;
								curflow = capacity[last] - arcflow[last];
								if (nodeflow[nodeu] < curflow)
									curflow = nodeflow[nodeu];
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
								if (nodep == nodeq)
									jmap[nodev] = nodeq;
							}
							controlg = false;
							if (nodeflow[nodeu] > 0)
								continue;
							if (capacity[last] == arcflow[last])
								last--;
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
							if (nodeflow[nodeu] < curflow)
								curflow = nodeflow[nodeu];
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

	public static void minimumSpanningTreeKruskal(int n, int m, int nodei[], int nodej[], int weight[], int treearc1[],
			int treearc2[]) {
		int i, index, index1, index2, index3, nodeu, nodev, nodew, len, nedge, treearc;
		int halfm, numarc, nedge2 = 0;
		int predecessor[] = new int[n + 1];

		for (i = 1; i <= n; i++)
			predecessor[i] = -1;
		// initialize the heap structure
		i = m / 2;
		while (i > 0) {
			index1 = i;
			halfm = m / 2;
			while (index1 <= halfm) {
				index = index1 + index1;
				index2 = ((index < m) && (weight[index + 1] < weight[index])) ? index + 1 : index;
				if (weight[index2] < weight[index1]) {
					nodeu = nodei[index1];
					nodev = nodej[index1];
					len = weight[index1];
					nodei[index1] = nodei[index2];
					nodej[index1] = nodej[index2];
					weight[index1] = weight[index2];
					nodei[index2] = nodeu;
					nodej[index2] = nodev;
					weight[index2] = len;
					index1 = index2;
				} else
					index1 = m;
			}
			i--;
		}
		nedge = m;
		treearc = 0;
		numarc = 0;
		while ((treearc < n - 1) && (numarc < m)) {
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
				} else {
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
				index2 = ((index < nedge) && (weight[index + 1] < weight[index])) ? index + 1 : index;
				if (weight[index2] < weight[index1]) {
					nodeu = nodei[index1];
					nodev = nodej[index1];
					len = weight[index1];
					nodei[index1] = nodei[index2];
					nodej[index1] = nodej[index2];
					weight[index1] = weight[index2];
					nodei[index2] = nodeu;
					nodej[index2] = nodev;
					weight[index2] = len;
					index1 = index2;
				} else
					index1 = nedge;
			}
		}
		treearc1[0] = treearc;
	}

	public static void minimalEquivalentGraph(int n, int m, int nodei[], int nodej[], boolean link[]) {
		int i, j, k, nodeu, nodev, n1, low, up, edges, index1, index2, high, kedge = 0;
		int nextnode[] = new int[n + 1];
		int ancestor[] = new int[n + 1];
		int descendant[] = new int[n + 1];
		int firstedges[] = new int[n + 2];
		int pointer[] = new int[m + 1];
		int endnode[] = new int[m + 1];
		boolean pathexist[] = new boolean[n + 1];
		boolean currentarc[] = new boolean[m + 1];
		boolean pexist[] = new boolean[1];
		boolean join, skip, hop;

		n1 = n + 1;
		// set up the forward star representation of the graph
		k = 0;
		for (i = 1; i <= n; i++) {
			firstedges[i] = k + 1;
			for (j = 1; j <= m; j++)
				if (nodei[j] == i) {
					k++;
					pointer[k] = j;
					endnode[k] = nodej[j];
					currentarc[k] = true;
				}
		}
		firstedges[n1] = m + 1;
		// compute number of descendants and ancestors of each node
		for (i = 1; i <= n; i++) {
			descendant[i] = 0;
			ancestor[i] = 0;
		}
		edges = 0;
		for (k = 1; k <= m; k++) {
			i = nodei[k];
			j = nodej[k];
			descendant[i]++;
			ancestor[j]++;
			edges++;
		}
		if (edges == n) {
			for (k = 1; k <= m; k++)
				link[pointer[k]] = currentarc[k];
			return;
		}
		index1 = 0;
		for (k = 1; k <= m; k++) {
			i = nodei[pointer[k]];
			j = nodej[pointer[k]];
			// check for the existence of an alternative path
			if (descendant[i] != 1) {
				if (ancestor[j] != 1) {
					currentarc[k] = false;
					minimalEqGraphFindp(n, m, n1, i, j, endnode, firstedges, currentarc, pexist, nextnode, pathexist);
					if (pexist[0]) {
						descendant[i]--;
						ancestor[j]--;
						index1++;
					} else
						currentarc[k] = true;
				}
			}
		}
		if (index1 == 0) {
			for (k = 1; k <= m; k++)
				link[pointer[k]] = currentarc[k];
			return;
		}
		high = 0;
		nodeu = n;
		nodev = n;
		// store the current best solution
		iterate: while (true) {
			for (k = 1; k <= m; k++)
				link[k] = currentarc[k];
			index2 = index1;
			if ((edges - index2) == n) {
				for (k = 1; k <= m; k++)
					currentarc[k] = link[k];
				for (k = 1; k <= m; k++)
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
					for (k = low; k <= up; k++)
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
								} else
									continue iterate;
							}
							while (true) {
								// backtrack move
								join = false;
								low = firstedges[nodeu];
								up = firstedges[nodeu + 1];
								if (up > low) {
									up--;
									for (k = low; k <= up; k++)
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
											minimalEqGraphFindp(n, m, n1, nodeu, nodev, endnode, firstedges, currentarc,
													pexist, nextnode, pathexist);
											if (pexist[0]) {
												descendant[nodeu]--;
												ancestor[nodev]--;
												index1++;
												skip = true;
											} else
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
									// check for the termination of the forward
									// move
									if (high - (n - nodeu) == 0)
										continue iterate;
								}
								if (nodev != n)
									nodev++;
								else {
									if (nodeu != n) {
										nodeu++;
										nodev = 1;
									} else
										continue iterate;
								}
							}
						}
					}
					if (!hop)
						high++;
				}
				hop = false;
				if (nodev != 1) {
					nodev--;
					continue;
				}
				if (nodeu == 1) {
					for (k = 1; k <= m; k++)
						currentarc[k] = link[k];
					for (k = 1; k <= m; k++)
						link[pointer[k]] = currentarc[k];
					return;
				}
				nodeu--;
				nodev = n;
			}
		}
	}

	public static boolean planarityTesting(int n, int m, int nodei[], int nodej[]) {
		int i, j, k, n2, m2, nm2, nmp2, m7n5, m22, m33, mtotal;
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
		int nextarc[] = new int[7 * m - 5 * n + 3];
		int arctop[] = new int[7 * m - 5 * n + 3];
		boolean middle[] = new boolean[1];
		boolean fail[] = new boolean[1];
		boolean examin[] = new boolean[m - n + 3];
		boolean arctype[] = new boolean[7 * m - 5 * n + 3];

		// check for the necessary condition
		if (m > 3 * n - 6)
			return false;
		n2 = n + n;
		m2 = m + m;
		nm2 = n + m + m;
		m22 = m + m + 2;
		m33 = m + m + m + 3;
		nmp2 = m - n + 2;
		m7n5 = 7 * m - 5 * n + 2;
		// set up graph representation
		for (i = 1; i <= n; i++)
			second[i] = 0;
		mtotal = n;
		for (i = 1; i <= m; i++) {
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
		for (i = 1; i <= n; i++) {
			mark[i] = 0;
			firstlow[i] = n + 1;
			secondlow[i] = n + 1;
		}
		snum[0] = 1;
		store1[0] = 0;
		mark[1] = 1;
		wkpathfind5[1] = 1;
		wkpathfind6[1] = 0;
		level[0] = 1;
		middle[0] = false;
		do {
			planarityDFS1(n, m, m2, nm2, level, middle, snum, store1, mark, firstlow, secondlow, wkpathfind5,
					wkpathfind6, stackarc, first, second);
		} while (level[0] > 1);
		for (i = 1; i <= n; i++)
			if (secondlow[i] >= mark[i])
				secondlow[i] = firstlow[i];
		// radix sort
		mtotal = n2;
		k = n2;
		for (i = 1; i <= n2; i++)
			sortn[i] = 0;
		for (i = 2; i <= m2; i += 2) {
			k++;
			sortptr1[k] = stackarc[i - 1];
			tnode = stackarc[i];
			sortptr2[k] = tnode;
			if (mark[tnode] < mark[sortptr1[k]]) {
				j = 2 * mark[tnode] - 1;
				sortn[k] = sortn[j];
				sortn[j] = k;
			} else {
				if (secondlow[tnode] >= mark[sortptr1[k]]) {
					j = 2 * firstlow[tnode] - 1;
					sortn[k] = sortn[j];
					sortn[j] = k;
				} else {
					j = 2 * firstlow[tnode];
					sortn[k] = sortn[j];
					sortn[j] = k;
				}
			}
		}
		for (i = 1; i <= n2; i++) {
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
		for (i = 2; i <= n; i++)
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
			planarityDFS2(n, m, m2, nm2, level, middle, snum, store1, mark, wkpathfind5, stackarc, first, second);
		} while (level[0] > 1);
		mtotal = n;
		for (i = 1; i <= m; i++) {
			j = i + i;
			node1 = stackarc[j - 1];
			node2 = stackarc[j];
			mtotal++;
			second[mtotal] = second[node1];
			second[node1] = mtotal;
			first[mtotal] = node2;
		}
		// path decomposition, construction of hte dependency graph
		store2[0] = 0;
		store3[0] = 0;
		store4[0] = 0;
		store5[0] = 0;
		initp[0] = 0;
		pnum[0] = 1;
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
		for (i = 1; i <= n2; i++)
			nodebegin[i] = 0;
		nexte[0] = m - n + 1;
		for (i = 1; i <= m7n5; i++)
			nextarc[i] = 0;
		snode[0] = n;
		descendant[1] = n;
		wkpathfind5[1] = 1;
		level[0] = 1;
		middle[0] = false;
		do {
			planarityDecompose(n, m, n2, m22, m33, nm2, nmp2, m7n5, level, middle, initp, snode, pnum, nexte, store2,
					store3, store4, store5, trail, descendant, nodebegin, wkpathfind5, start, finish, first, second,
					wkpathfind1, wkpathfind2, wkpathfind3, wkpathfind4, nextarc, arctop, arctype);
		} while (level[0] > 1);
		// perform two-coloring
		pnum[0]--;
		for (i = 1; i <= nmp2; i++)
			paint[i] = 0;
		j = pnum[0] + 1;
		for (i = 2; i <= j; i++)
			examin[i] = true;
		tnum = 1;
		while (tnum <= pnum[0]) {
			wkpathfind5[1] = tnum;
			paint[tnum] = 1;
			examin[tnum] = false;
			level[0] = 1;
			middle[0] = false;
			do {
				planarityTwoColoring(m, nmp2, m7n5, level, middle, fail, wkpathfind5, paint, nextarc, arctop, examin,
						arctype);
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
		for (i = 1; i <= pnum[0]; i++) {
			qnode = start[i + 1];
			tnode = finish[i + 1];
			while (qnode <= stackcolor1[aux1 + 2])
				aux1 -= 2;
			while (qnode <= stackcolor2[aux2 + 2])
				aux2 -= 2;
			while (qnode <= stackcolor3[aux3 + 2])
				aux3 -= 2;
			while (qnode <= stackcolor4[aux4 + 2])
				aux4 -= 2;
			if (paint[i] == 1) {
				if (finish[trail[qnode] + 1] != tnode) {
					if (tnode < stackcolor2[aux2 + 2])
						return false;
					if (tnode < stackcolor3[aux3 + 2])
						return false;
					aux3 += 2;
					stackcolor3[aux3 + 1] = i;
					stackcolor3[aux3 + 2] = tnode;
				} else {
					if ((tnode < stackcolor3[aux3 + 2]) && (start[stackcolor3[aux3 + 1] + 1] <= descendant[qnode]))
						return false;
					aux1 += 2;
					stackcolor1[aux1 + 1] = i;
					stackcolor1[aux1 + 2] = qnode;
				}
			} else {
				if (finish[trail[qnode] + 1] != tnode) {
					if (tnode < stackcolor1[aux1 + 2])
						return false;
					if (tnode < stackcolor4[aux4 + 2])
						return false;
					aux4 += 2;
					stackcolor4[aux4 + 1] = i;
					stackcolor4[aux4 + 2] = tnode;
				} else {
					if ((tnode < stackcolor4[aux4 + 2]) && (start[stackcolor4[aux4 + 1] + 1] <= descendant[qnode]))
						return false;
					aux2 += 2;
					stackcolor2[aux2 + 1] = i;
					stackcolor2[aux2 + 2] = qnode;
				}
			}
		}
		return true;
	}

	public static void randomBipartiteGraph(int n1, int n2, int m, long seed, int nodei[], int nodej[]) {
		int n, nodea, nodeb, nodec, numedges;
		boolean adj[][] = new boolean[n1 + n2 + 1][n1 + n2 + 1];
		boolean temp;
		Random ran = new Random(seed);

		n = n1 + n2;
		// initialize the adjacency matrix
		for (nodea = 1; nodea <= n; nodea++)
			for (nodeb = 1; nodeb <= n; nodeb++)
				adj[nodea][nodeb] = false;

		if (m != 0) {
			if (m > n1 * n2)
				m = n1 * n2;
			numedges = 0;
			// generate a simple bipartite graph with exactly m edges
			while (numedges < m) {
				// generate a random integer in interval [1, n1]
				nodea = (int) (1 + ran.nextDouble() * n1);
				// generate a random integer in interval [n1+1, n]
				nodeb = (int) (n1 + 1 + ran.nextDouble() * n2);
				if (!adj[nodea][nodeb]) {
					// add the edge (nodei,nodej)
					adj[nodea][nodeb] = adj[nodeb][nodea] = true;
					numedges++;
				}
			}
		} else {
			// generate a random adjacency matrix with edges from
			// nodes of group [1, n1] to nodes of group [n1+1, n]
			for (nodea = 1; nodea <= n1; nodea++)
				for (nodeb = n1 + 1; nodeb <= n; nodeb++)
					adj[nodea][nodeb] = adj[nodeb][nodea] = (ran.nextInt(2) == 0) ? false : true;
		}
		// random permutation of rows and columns
		for (nodea = 1; nodea <= n; nodea++) {
			nodec = (int) (nodea + ran.nextDouble() * (n + 1 - nodea));
			for (nodeb = 1; nodeb <= n; nodeb++) {
				temp = adj[nodec][nodeb];
				adj[nodec][nodeb] = adj[nodea][nodeb];
				adj[nodea][nodeb] = temp;
			}
			for (nodeb = 1; nodeb <= n; nodeb++) {
				temp = adj[nodeb][nodec];
				adj[nodeb][nodec] = adj[nodeb][nodea];
				adj[nodeb][nodea] = temp;
			}
		}
		numedges = 0;
		for (nodea = 1; nodea <= n; nodea++)
			for (nodeb = nodea + 1; nodeb <= n; nodeb++)
				if (adj[nodea][nodeb]) {
					numedges++;
					nodei[numedges] = nodea;
					nodej[numedges] = nodeb;
				}
		nodei[0] = numedges;
	}

	public static int randomConnectedGraph(int n, int m, long seed, boolean weighted, int minweight, int maxweight,
			int nodei[], int nodej[], int weight[]) {
		int maxedges, nodea, nodeb, numedges, temp;
		int permute[] = new int[n + 1];
		boolean adj[][] = new boolean[n + 1][n + 1];
		Random ran = new Random(seed);

		// initialize the adjacency matrix
		for (nodea = 1; nodea <= n; nodea++)
			for (nodeb = 1; nodeb <= n; nodeb++)
				adj[nodea][nodeb] = false;
		numedges = 0;
		// check for valid input data
		if (m < (n - 1))
			return 1;
		maxedges = (n * (n - 1)) / 2;
		if (m > maxedges)
			return 2;

		// generate a random spanning tree by the greedy method
		randomPermutation(n, ran, permute);
		for (nodea = 2; nodea <= n; nodea++) {
			nodeb = ran.nextInt(nodea - 1) + 1;
			numedges++;
			nodei[numedges] = permute[nodea];
			nodej[numedges] = permute[nodeb];
			adj[permute[nodea]][permute[nodeb]] = true;
			adj[permute[nodeb]][permute[nodea]] = true;
			if (weighted)
				weight[numedges] = (int) (minweight + ran.nextDouble() * (maxweight + 1 - minweight));
		}
		// add the remaining edges randomly
		while (numedges < m) {
			nodea = ran.nextInt(n) + 1;
			nodeb = ran.nextInt(n) + 1;
			if (nodea == nodeb)
				continue;
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
					weight[numedges] = (int) (minweight + ran.nextDouble() * (maxweight + 1 - minweight));
			}
		}
		return 0;
	}

	public static int randomGraph(int n, int m, long seed, boolean simple, boolean directed, boolean acyclic,
			boolean weighted, int minweight, int maxweight, int nodei[], int nodej[], int weight[]) {
		int maxedges, nodea, nodeb, numedges, temp;
		int dagpermute[] = new int[n + 1];
		boolean adj[][] = new boolean[n + 1][n + 1];
		Random ran = new Random(seed);

		// initialize the adjacency matrix
		for (nodea = 1; nodea <= n; nodea++)
			for (nodeb = 1; nodeb <= n; nodeb++)
				adj[nodea][nodeb] = false;
		numedges = 0;
		// check for valid input data
		if (simple) {
			maxedges = n * (n - 1);
			if (!directed)
				maxedges /= 2;
			if (m > maxedges)
				return 1;
		}
		if (acyclic) {
			maxedges = (n * (n - 1)) / 2;
			if (m > maxedges)
				return 1;
			randomPermutation(n, ran, dagpermute);
		}
		while (numedges < m) {
			nodea = ran.nextInt(n) + 1;
			nodeb = ran.nextInt(n) + 1;
			if (simple || acyclic)
				if (nodea == nodeb)
					continue;
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
			if ((!simple) || (simple && (!adj[nodea][nodeb]))) {
				numedges++;
				nodei[numedges] = nodea;
				nodej[numedges] = nodeb;
				adj[nodea][nodeb] = true;
				if (weighted)
					weight[numedges] = (int) (minweight + ran.nextDouble() * (maxweight + 1 - minweight));
			}
		}
		return 0;
	}

	public static int randomHamiltonGraph(int n, int m, long seed, boolean directed, boolean weighted, int minweight,
			int maxweight, int nodei[], int nodej[], int weight[]) {
		int k, maxedges, nodea, nodeb, numedges, temp;
		int permute[] = new int[n + 1];
		boolean adj[][] = new boolean[n + 1][n + 1];
		Random ran = new Random(seed);

		// initialize the adjacency matrix
		for (nodea = 1; nodea <= n; nodea++)
			for (nodeb = 1; nodeb <= n; nodeb++)
				adj[nodea][nodeb] = false;
		// adjust value of m if needed
		if (m < n)
			return 1;
		maxedges = n * (n - 1);
		if (!directed)
			maxedges /= 2;
		if (m > maxedges)
			return 2;
		numedges = 0;
		// generate a random permutation
		randomPermutation(n, ran, permute);
		// obtain the initial cycle
		for (k = 1; k <= n; k++) {
			if (k == n) {
				nodea = permute[n];
				nodeb = permute[1];
			} else {
				nodea = permute[k];
				nodeb = permute[k + 1];
			}
			numedges++;
			nodei[numedges] = nodea;
			nodej[numedges] = nodeb;
			adj[nodea][nodeb] = true;
			if (!directed)
				adj[nodeb][nodea] = true;
			if (weighted)
				weight[numedges] = (int) (minweight + ran.nextDouble() * (maxweight + 1 - minweight));
		}
		// add the remaining edges randomly
		while (numedges < m) {
			nodea = ran.nextInt(n) + 1;
			nodeb = ran.nextInt(n) + 1;
			if (nodea == nodeb)
				continue;
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
					weight[numedges] = (int) (minweight + ran.nextDouble() * (maxweight + 1 - minweight));
			}
		}
		return 0;
	}

	public static int randomRegularGraph(int n, int degree, long seed, int nodei[], int nodej[]) {
		int i, j, numedges, p, q, r = 0, s = 0, u, v = 0;
		int permute[] = new int[n + 1];
		int deg[] = new int[n + 1];
		boolean adj[][] = new boolean[n + 1][n + 1];
		boolean more;
		Random ran = new Random(seed);

		// initialize the adjacency matrix
		for (i = 1; i <= n; i++)
			for (j = 1; j <= n; j++)
				adj[i][j] = false;
		// initialize the degree of each node
		for (i = 1; i <= n; i++)
			deg[i] = 0;
		// check input data consistency
		if ((degree % 2) != 0)
			if ((n % 2) != 0)
				return 1;
		if (n <= degree)
			return 2;
		// generate the regular graph
		iterate: while (true) {
			randomPermutation(n, ran, permute);
			more = false;
			// find two non-adjacent nodes each has less than required degree
			u = 0;
			for (i = 1; i <= n; i++)
				if (deg[permute[i]] < degree) {
					v = permute[i];
					more = true;
					for (j = i + 1; j <= n; j++) {
						if (deg[permute[j]] < degree) {
							u = permute[j];
							if (!adj[v][u]) {
								// add edge (u,v) to the random graph
								adj[v][u] = adj[u][v] = true;
								deg[v]++;
								deg[u]++;
								continue iterate;
							} else {
								// both r & s are less than the required degree
								r = v;
								s = u;
							}
						}
					}
				}
			if (!more)
				break;
			if (u == 0) {
				r = v;
				// node r has less than the required degree,
				// find two adjacent nodes p and q non-adjacent to r.
				for (i = 1; i <= n - 1; i++) {
					p = permute[i];
					if (r != p)
						if (!adj[r][p])
							for (j = i + 1; j <= n; j++) {
								q = permute[j];
								if (q != r)
									if (adj[p][q] && (!adj[r][q])) {
										// add edges (r,p) & (r,q), delete edge
										// (p,q)
										adj[r][p] = adj[p][r] = true;
										adj[r][q] = adj[q][r] = true;
										adj[p][q] = adj[q][p] = false;
										deg[r]++;
										deg[r]++;
										continue iterate;
									}
							}
				}
			} else {
				// nodes r and s of less than required degree, find two
				// adjacent nodes p & q such that (p,r) & (q,s) are not edges.
				for (i = 1; i <= n; i++) {
					p = permute[i];
					if ((p != r) && (p != s))
						if (!adj[r][p])
							for (j = 1; j <= n; j++) {
								q = permute[j];
								if ((q != r) && (q != s))
									if (adj[p][q] && (!adj[s][q])) {
										// remove edge (p,q), add edges (p,r) &
										// (q,s)
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
		for (i = 1; i <= n; i++)
			for (j = i + 1; j <= n; j++)
				if (adj[i][j]) {
					numedges++;
					nodei[numedges] = i;
					nodej[numedges] = j;
				}
		return 0;
	}

	public static void randomPermutation(int n, Random ran, int perm[]) {
		int i, j, k;

		for (i = 1; i <= n; i++)
			perm[i] = i;
		for (i = 1; i <= n; i++) {
			j = (int) (i + ran.nextDouble() * (n + 1 - i));
			k = perm[i];
			perm[i] = perm[j];
			perm[j] = k;
		}
	}

	public static void stronglyConnectedComponents(int n, int m, int nodei[], int nodej[], int component[]) {
		int i, j, k, series, stackpointer, numcompoents, p, q, r;
		int backedge[] = new int[n + 1];
		int parent[] = new int[n + 1];
		int sequence[] = new int[n + 1];
		int stack[] = new int[n + 1];
		int firstedges[] = new int[n + 2];
		int endnode[] = new int[m + 1];
		boolean next[] = new boolean[n + 1];
		boolean trace[] = new boolean[n + 1];
		boolean fresh[] = new boolean[m + 1];
		boolean skip, found;

		// set up the forward star representation of the graph
		firstedges[1] = 0;
		k = 0;
		for (i = 1; i <= n; i++) {
			for (j = 1; j <= m; j++)
				if (nodei[j] == i) {
					k++;
					endnode[k] = nodej[j];
				}
			firstedges[i + 1] = k;
		}
		for (j = 1; j <= m; j++)
			fresh[j] = true;
		// initialize
		for (i = 1; i <= n; i++) {
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
				if (!trace[p])
					break;
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
				for (q = 1; q <= n; q++) {
					// find an unprocessed edge (p,q)
					found = false;
					for (i = firstedges[p] + 1; i <= firstedges[p + 1]; i++)
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
						} else {
							if (trace[q]) {
								if (sequence[q] < sequence[p] && next[q]) {
									backedge[p] = (backedge[p] < sequence[q]) ? backedge[p] : sequence[q];
								}
							}
						}
						skip = true;
						break;
					}
				}
				if (skip)
					continue;
				if (backedge[p] == sequence[p]) {
					numcompoents++;
					while (true) {
						r = stack[stackpointer];
						stackpointer--;
						next[r] = false;
						component[r] = numcompoents;
						if (r == p)
							break;
					}
				}
				if (parent[p] != 0) {
					backedge[parent[p]] = (backedge[parent[p]] < backedge[p]) ? backedge[parent[p]] : backedge[p];
					p = parent[p];
				} else
					break;
			}
		}
	}

	static private void minimalEqGraphFindp(int n, int m, int n1, int nodeu, int nodev, int endnode[], int firstedges[],
			boolean currentarc[], boolean pexist[], int nextnode[], boolean pathexist[]) {
		/* this method is used internally by minimalEquivalentGraph */

		// determine if a path exists from nodeu to nodev by Yen's algorithm

		int i, j, k, i2, j2, low, up, kedge = 0, index1, index2, index3;
		boolean join;

		// initialization
		for (i = 1; i <= n; i++) {
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
				for (k = low; k <= up; k++)
					if (endnode[k] == j) {
						join = true;
						kedge = k;
						break;
					}
			}
			if (join)
				if (currentarc[kedge])
					pathexist[j] = true;
			if (pathexist[j]) {
				index3 = i + 1;
				if (index3 <= index2) {
					for (i2 = index3; i2 <= index2; i2++) {
						j2 = nextnode[i2];
						join = false;
						low = firstedges[index1];
						up = firstedges[index1 + 1];
						if (up > low) {
							up--;
							for (k = low; k <= up; k++)
								if (endnode[k] == j2) {
									join = true;
									kedge = k;
									break;
								}
						}
						if (join)
							if (currentarc[kedge])
								pathexist[j2] = true;
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
				if (index2 > 1)
					continue;
				join = false;
				low = firstedges[index1];
				up = firstedges[index1 + 1];
				if (up > low) {
					up--;
					for (k = low; k <= up; k++)
						if (endnode[k] == nodev) {
							join = true;
							kedge = k;
							break;
						}
				}
				pexist[0] = false;
				if (join)
					if (currentarc[kedge])
						pexist[0] = true;
				return;
			}
			i++;
			if (i <= index2)
				continue;
			pexist[0] = false;
			return;
		}
	}

	static private void planarityDFS1(int n, int m, int m2, int nm2, int level[], boolean middle[], int snum[],
			int store1[], int mark[], int firstlow[], int secondlow[], int wkpathfind5[], int wkpathfind6[],
			int stackarc[], int first[], int second[]) {
		/* this method is used internally by planarityTesting */

		int pnode = 0, qnode = 0, tnode = 0, tmp1, tmp2;
		boolean skip;

		skip = false;
		if (middle[0])
			skip = true;
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
					store1[0] += 2;
					stackarc[store1[0] - 1] = qnode;
					stackarc[store1[0]] = tnode;
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
					} else {
						if (firstlow[tnode] == firstlow[qnode]) {
							tmp1 = secondlow[tnode];
							tmp2 = secondlow[qnode];
							secondlow[qnode] = (tmp1 < tmp2 ? tmp1 : tmp2);
						} else {
							tmp1 = firstlow[tnode];
							tmp2 = secondlow[qnode];
							secondlow[qnode] = (tmp1 < tmp2 ? tmp1 : tmp2);
						}
					}
				} else {
					if (mark[tnode] < firstlow[qnode]) {
						secondlow[qnode] = firstlow[qnode];
						firstlow[qnode] = mark[tnode];
					} else {
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

	static private void planarityDFS2(int n, int m, int m2, int nm2, int level[], boolean middle[], int snum[],
			int store1[], int mark[], int wkpathfind5[], int stackarc[], int first[], int second[]) {
		/* this method is used internally by planarityTesting */

		int qnode, tnode;

		if (middle[0]) {
			tnode = wkpathfind5[level[0]];
			level[0]--;
			qnode = wkpathfind5[level[0]];
			store1[0] += 2;
			stackarc[store1[0] - 1] = mark[qnode];
			stackarc[store1[0]] = mark[tnode];
		} else
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
			stackarc[store1[0] - 1] = mark[qnode];
			stackarc[store1[0]] = mark[tnode];
		}
		middle[0] = true;
	}

	static private void planarityDecompose(int n, int m, int n2, int m22, int m33, int nm2, int nmp2, int m7n5,
			int level[], boolean middle[], int initp[], int snode[], int pnum[], int nexte[], int store2[],
			int store3[], int store4[], int store5[], int trail[], int descendant[], int nodebegin[], int wkpathfind5[],
			int start[], int finish[], int first[], int second[], int wkpathfind1[], int wkpathfind2[],
			int wkpathfind3[], int wkpathfind4[], int nextarc[], int arctop[], boolean arctype[]) {
		/* this method is used internally by planarityTesting */

		int node1, node2, qnode = 0, qnode2, tnode = 0, tnode2;
		boolean ind, skip;

		skip = false;
		if (middle[0])
			skip = true;
		if (!skip)
			qnode = wkpathfind5[level[0]];
		while ((second[qnode] != 0) || skip) {
			if (!skip) {
				tnode = first[second[qnode]];
				second[qnode] = second[second[qnode]];
				if (initp[0] == 0)
					initp[0] = qnode;
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
				while ((nodebegin[qnode2 - 1] > wkpathfind3[store4[0] + 2]) && (qnode < wkpathfind3[store4[0] + 2])
						&& (nodebegin[qnode2] < wkpathfind3[store4[0] + 1])) {
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
				if (ind)
					store4[0] += 3;
				nodebegin[qnode2 - 1] = 0;
				nodebegin[qnode2] = 0;
			} else {
				start[pnum[0] + 1] = initp[0];
				finish[pnum[0] + 1] = tnode;
				ind = false;
				if (wkpathfind1[store2[0] + 2] != 0) {
					store3[0] += 2;
					wkpathfind2[store3[0] + 1] = wkpathfind1[store2[0] + 1];
					wkpathfind2[store3[0] + 2] = wkpathfind1[store2[0] + 2];
				}
				if (finish[wkpathfind1[store2[0] + 1] + 1] != tnode) {
					while (tnode < wkpathfind2[store3[0] + 2]) {
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
						ind = true;
						store3[0] -= 2;
					}
					if (ind)
						store3[0] += 2;
					ind = false;
					while ((tnode < wkpathfind3[store4[0] + 3]) && (initp[0] < wkpathfind3[store4[0] + 2])) {
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
					while ((tnode < wkpathfind4[store5[0] + 3]) && (initp[0] < wkpathfind4[store5[0] + 2]))
						store5[0] -= 3;
					tnode2 = tnode + tnode;
					if (initp[0] > nodebegin[tnode2 - 1]) {
						nodebegin[tnode2 - 1] = initp[0];
						nodebegin[tnode2] = pnum[0];
					}
					store4[0] += 3;
					wkpathfind3[store4[0] + 1] = pnum[0];
					wkpathfind3[store4[0] + 2] = initp[0];
					wkpathfind3[store4[0] + 3] = tnode;
					store5[0] += 3;
					wkpathfind4[store5[0] + 1] = pnum[0];
					wkpathfind4[store5[0] + 2] = initp[0];
					wkpathfind4[store5[0] + 3] = tnode;
				} else {
					while ((tnode < wkpathfind4[store5[0] + 3]) && (initp[0] < wkpathfind4[store5[0] + 2])
							&& (wkpathfind4[store5[0] + 2] <= descendant[initp[0]])) {
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
					if (ind)
						store5[0] += 3;
				}
				if (qnode != initp[0]) {
					store2[0] += 2;
					wkpathfind1[store2[0] + 1] = pnum[0];
					wkpathfind1[store2[0] + 2] = initp[0];
				}
				pnum[0]++;
				initp[0] = 0;
			}
		}
		middle[0] = true;
	}

	static private void planarityTwoColoring(int m, int nmp2, int m7n5, int level[], boolean middle[], boolean fail[],
			int wkpathfind5[], int paint[], int nextarc[], int arctop[], boolean examin[], boolean arctype[]) {
		/* this method is used internally by planarityTesting */

		int link, qnode, tnode;
		boolean dum1, dum2;

		fail[0] = false;
		if (middle[0]) {
			level[0]--;
			qnode = wkpathfind5[level[0]];
		} else
			qnode = wkpathfind5[level[0]];
		while (nextarc[qnode] != 0) {
			link = nextarc[qnode];
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

}