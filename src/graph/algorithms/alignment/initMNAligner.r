



for (i in 1:length(qVal)){
   qR <- qRow[i]
   qC <- qCol[i]
   qV <- qVal[i]
   Q[qR+1,qC+1] <- qV
}