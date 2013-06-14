#rm(btmp, ctmp, atmp, qtmp)
#
#
#for (i in qVal){
#   qR <- qRow[i]
#   qC <- qCol[i]
#   qV <- qVal[i]
#   Q[qR,qC] <- qV
#}
#


#
## spsolpq ##
#

#
## start phase 1 ##
#

toler <- 1.e-5
betaVal <- 0.8
alphaVal <- 0.95

m <- dim(A)[1]
n <- dim(A)[2]

## Search for a feasible point: #

a <- bb - A %*% matrix(1,n,1)
x <- matrix(1,n+1,1)
z <- c(0)
ob <- x[n+1,1]
obhis <- c(ob)
gap <- ob-z
    
while(gap >= toler){

	#
	## spphase1 ##
	#
		dx <- matrix(1,n,1) / x[1:n]
		DD <- diag(as.vector(dx*dx), n, n)
		ans1 <- rbind(cbind(DD, t(A)), cbind(A, matrix(0,m,m)))
		ans2 <- rbind(cbind(dx, matrix(0,n,1)), cbind(matrix(0,m,1), a))
		ans <- solve(ans1, ans2)

		y1 <- ans[(n+1):(n+m),1]
		y2 <- ans[(n+1):(n+m),2]
		rm(dx, ans, DD)
		w1 <- (1/ob - crossprod(a,y1)) / (1/ob^2 - crossprod(a,y2))
		w2 <- 1 / (1/ob^2 - crossprod(a,y2))
		y1 <- y1 - (w1 %*% y2)
		y2 <- -(w2 %*% y2)

		w1 <- t(bb) %*% t(y1)
		w2 <- t(bb) %*% t(y2)
		y1 <- y1 / as.vector(1+w1)
		y2 <- y2 - (w2 * as.vector(y1))
		u <- rbind(x[1:n] * t((-y2) %*% A), x[n+1] * (1 - y2 %*% a), w2/(1+w1))
		v <- rbind(x[1:n] * t((y1) %*% A), x[n+1] * (y1 %*% a), 1/(1+w1))

		#
		# update the dual and the objective lower bound #
		#
		if(min(u-z*v) >= 0){
			y <- y2 + z*y1
			z <- (t(bb)) %*% (t(y))
		}
		rm(y1,y2,w1,w2)
		
		# find the descent direction #
		
		u <- u - z*v - ((ob-z) / (n+2)) * matrix(1,n+2,1)
		nora <- max(u)
		
		# update the solution along the descent direction #

		if(nora == u[n+1]){
			alphaVal <- 1.0
		}
		v <- matrix(1,n+2,1) - (alphaVal/nora)*u
		x <- x * v[1:(n+1)] / v[n+2]
		
	#
	## end of spphase1 ##
	#
  
	ob <- x[n+1]
	obhis <- c(obhis, ob)
	gap <- ob-z
	if(z > 0){
		gap <- -1
  		# The system has no feasible solution. #
  		print('The system has no feasible solution.')
  		return
 	}
} #end while
rm(a, ans1,ans2)

#
## Start Phase 2 ##
#

# search for an optimal solution #
alphaVal <- 0.9
x <- x[1:n]
comp <- matrix(runif(n), n, 1)
#comp <- matrix((1:n)/(n+1), n, 1)
ans1 <- rbind(cbind(diag(1,n,n), t(A)) , cbind(A, matrix(0,m,m)))
ans2 <- rbind(comp, matrix(0,m,1))
ans <- solve(ans1, ans2)
comp <- ans[1:n]
rm(ans)
nora <- min(comp/x)
if(nora < 0){
	nora <- (-.01)/nora
} else{
	nora <- max(comp/x)
	if(nora == 0){
		# the problem has a unique feasible point #
		return
	}
	nora <- 0.01/nora
}
x <- cbind(x + nora*comp)
obvalue <- t(x) %*% (Q %*% x) / (2 + t(cc) %*% x)
obhis <- c(obvalue)
lower <- -Inf
zhis <- c(lower)
gap <- 1
lamda <- max(1, abs(obvalue)/sqrt(sqrt(n)))

iter <- 0
while(gap >= toler){
	
	iter <- iter+1
  
	#
	# spphase2
	#
  
		lamda <- (1.0 - betaVal)*lamda
		go <- 0
		gg <- Q %*% x+ cc
		XX <- diag(as.vector(x),n,n)
		AA <- A %*% XX
		XX <- XX %*% Q %*% XX
		EPS <- 2^(-52)
		
		# repeatly solve an ellipsoid constraind QP problem by solving
		# a linear system equation until find a positive solution
   
		while(go<=0){
    
			ans3 <- rbind(cbind(XX+lamda*diag(1,n,n), t(AA)) , cbind(AA, matrix(0,m,m)))
			ans4 <- rbind((-x)*gg, matrix(0,m,1))
			u <- solve(ans3, ans4)
			xx <- x + x*u[1:n]
			go <- min(xx)
			if(go > 0){
				ob <- t(xx)%*%Q%*%xx / 2+t(cc)%*%xx
				go <- min(go, obvalue-ob+EPS)      
			}
			lamda <- 2*lamda
			if(lamda >= (1+abs(obvalue))/toler){
				# the problem seems unbounded
				return
			}
    
		} #end while
  
		y <- -u[(n+1):(n+m)]
		u <- u[1:n]
		nora <- min(u)
		if(nora < 0){
			nora <- -alphaVal/nora
		}else if(nora == 0){
			nora <- alphaVal
		} else{
			nora <- -Inf
		}

		u <- x * u
		w1 <- t(u) %*% Q %*% u
		w2 <- -t(u) %*% gg
		if(w1 > 0){
			nora <- min(w2/w1, nora)
		}
		if(nora == Inf){
			ob <- -Inf
		}else{
			x <- x+nora*u
			ob <- t(x) %*% Q %*% x / 2 + t(cc) %*% x
		}
		rm(u, dx, xx, DD, w1, w2)
  
	#
	# end of spphase2
	#
  
	if(ob == -Inf){
		gap <- 0
		#the problem is unbounded
		return
	}else{
		obhis <- c(obhis, ob)
		comp <- Q %*% x + cc - t(A) %*% y
		if(min(comp) >= 0){
			zhis[iter+1] <- ob - t(x) %*% comp
			lower <- zhis[iter+1]
			gap <- (ob-lower)/(1+abs(ob))
			obvalue = ob
		}else{
			zhis[iter+1] <- zhis[iter]
			lower <- zhis[iter+1]
			gap <- (obvalue-ob)/(1+abs(ob))
			obvalue <- ob
		}
  }

} #end while

solution <- (t(matrix(x[1:(DimA*DimB)], DimB, DimA)))

