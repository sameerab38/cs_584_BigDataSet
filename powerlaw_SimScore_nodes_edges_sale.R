setwd("C:/Users/samee/Documents/CS584/code_javaSE/project/Bigdata_Set")
dist = read.csv("purchase_Egonets_NodeAndEdge_Count.csv")
str(dist)
fit = lm( log(dist$EdgeCount,10) ~ log(dist$NodeCount,10))
fit
plot(y = log(dist$EdgeCount,10), x = log(dist$NodeCount,10), pch=4, col= "red")
abline(fit$coefficients[1], fit$coefficients[2]) 
C = 10^fit$coefficients[1]
Theta = fit$coefficients[2]
dist$outlierscore = rep(0,dim(dist)[1])
for(i in 1:dim(dist)[1])
{
dist$outlierscore[i] = max(C*(dist[i,2]^Theta), dist[i,3]  ) / min(C*(dist[i,2]^Theta), dist[i,3] ) * log(abs(C*(dist[i,2]^Theta)- dist[i,3] ) +1 , 10);
}
text(log(dist$NodeCount,10), log(dist$EdgeCount,10), labels = sprintf("%.2f",dist$outlierscore) , adj = 1, cex = 0.5)
write.csv(dist,file="purchase_sim_outlierscores.csv")
setwd("C:/Users/samee/Documents/CS584/code_javaSE/project/Bigdata_Set")
dist = read.csv("sale_Egonets_NodeAndEdge_Count.csv")
str(dist)
fit = lm( log(dist$EdgeCount,10) ~ log(dist$NodeCount,10))
fit
plot(y = log(dist$EdgeCount,10), x = log(dist$NodeCount,10), pch=4, col= "red")
abline(fit$coefficients[1], fit$coefficients[2])
savehistory("powerlaw_SimScore_nodes_edges_sale.R")
