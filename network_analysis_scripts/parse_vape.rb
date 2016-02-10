######################################################################
#
# ruby script to parse vaping tweets for Matt's network study
#
# input: Twitter data
#
# output: CSV file with weighted edgelist
#
# notes:
# - manage encoding issues
# - also output top edges
#
######################################################################

#/usr/bin/ruby -w

vape_file = File.new("vape_april.csv","r")
# combined = File.new("combined.csv","w")

count_good = 0
count_bad = 0

people = Hash.new
edge_list = Hash.new

while (line = vape_file.gets)
  #line.encode!('UTF-8', 'binary', invalid: :replace, undef: :replace, replace: '')
  if line =~ /"\d+-\d+-\d+ \d+:\d+:\d+","RT @([\w\d]+):(.+)",([\w\d]+)/
    count_good += 1
    original = $1
    creator = $3
    edge = original + "," + creator
    
    # store edges
    if (edge_list.has_key?(edge))
      edge_old_count = edge_list[edge]
      edge_new_count = edge_old_count + 1
      edge_list[edge] = edge_new_count
    else
      edge_list[edge] = 1
    end
    
    # store tweeters
    if (people.has_key?(creator))
      old_count = people[creator]
      new_count = old_count + 1
      people[creator] = new_count
    else
      people[creator] = 1
    end
  else
    count_bad += 1
  end
end

puts "good:#{count_good}"
puts "bad:#{count_bad}"
puts "people:#{people.size}"
puts "edges:#{edge_list.size}"

retweet_edges = File.new("retweet_edges.csv","w")
edge_list.each do |k,v|
  retweet_edges.puts "#{k},#{v}"
end

puts Hash[people.sort_by {|k,v| v}.reverse[0..10]]