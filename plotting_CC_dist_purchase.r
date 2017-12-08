getwd()
setwd("C:/Users/samee/Documents/CS584/code_javaSE/project")
dist = read.csv("purchase_all_cc_size_distribution.csv",h=F)
hist(dist)
str(dist)
barplot(table(dist$V1))

h = hist(dist$V1)
h$density = h$counts/sum(h$counts)*100
plot(h,freq=FALSE, ylim=c(0,100),ylab='%' , xlab='Number of nodes in Connected Components',main="Purchase CC's",col="grey")

library(ggplot2)
ggplot(dist, aes(x=factor(V1)) ,stat='count') + 
  geom_histogram(fill="lightgreen", color="grey50", stat="count", aes( y=..count../sum(..count..)))+ylab('%')+xlab('number of cc')
savehistory("plotting_CC_dist_purchase.r")
