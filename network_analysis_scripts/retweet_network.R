######################################################################
#
# R script to take edgelist and conduct analyses for project to:
# - run community detection
# - calculate clustering coefficient for each community
#
# input:
# - network file
#
# output:
# - files that include sizes of each cluster, and clustering coefficient of each cluster
#
# notes:
# - change loops to lapply
# - include some code to conduct quick plots
#
######################################################################

# library(sna)
library(igraph)

# load file and simplify
g <- read.graph("retweet_edges_R_unique.csv",format="ncol")
g <- simplify(g)

# extract the giant component
cl <- clusters(g)
gc <- induced.subgraph(g,which(cl$membership==which.max(cl$csize)))

# community detection (~30 minutes)
wc <- fastgreedy.community(gc)
# system.time(wc2 <- walktrap.community(gc))

mb <- membership(wc2)
c_cc = NULL
# create the size of each cluster
c_sizes = NULL

# calculate CC and sizes for each cluster
for (i in 1:length(wc2)) {
  # pull the subgraph for each module
  subg <- induced_subgraph(g,wc2[[i]])
  # get the CC for that module and store
  # c_cc[[i]] <- transitivity(subg)
  c_cc = c(c_cc,transitivity(subg))
  # get the cluster size
  c_sizes = c(c_sizes,length(wc2[[i]]))
}

cc_ordered <- order(c_cc,decreasing=TRUE)
for (i in 1:length(wc2)) {
    sink("output_sizes.txt",append=TRUE)
    message("counter:",cc_ordered[i]," size:",c_sizes[cc_ordered[i]]," cc:",c_cc[cc_ordered[i]])
    sink()

    sink("output_members.txt",append=TRUE)
    cat(paste(names(which(mb[] == cc_ordered[i])),",",cc_ordered),sep="\n")
    sink()
}

# test the plotting size/clustering coefficient
#
# counter = 0
# for (j in 1:length(output)) {
#   if (c_sizes[j] > 10) {
#     counter = counter + 1
#     message(j,":",c_cc[[j]])
#     x[j] <- c_sizes[j]
#   } else {
#     x[j] = NA
#   }
# }
# 
# y = NULL
# for (k in 1:length(c_cc)) {
#   if (c_cc[[k]] < 0.001) {
#     y = c(y,NA)
#   } else {
#     y = c(y,c_cc[[k]])
#   }
# }