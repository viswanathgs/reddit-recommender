#!/usr/bin/python -tt

def sort_dump(infile, outfile, max_lines = -1):
	'''
		Sorts the first max_lines of original dump based on 
		account id and sub-reddit id.
	'''

	fin = open(infile, 'r')
	lines = []
	line_count = 0
	for line in fin:
		line = line.split()
		lines.append([line[0], line[2], line[3]])
		line_count += 1
		if line_count == max_lines:
			break

	lines = sorted(lines, key = lambda x: (x[0], x[1]))
	fin.close()

	fout = open(outfile, 'w')
	for line in lines:
		fout.write("%s\n" % '\t'.join(line))
	fout.close()

def filter_top_subreddits(infile, outfile, max_subreddits):
	'''
		Takes a file that contains lines of (account_id, subreddit_id, affinity) tuples,
		and retains only those tuples whose subreddit_id is among the max_subreddits
		most frequently occurring subreddits.
	'''

	fin = open(infile, 'r')
	subreddit_counts = {}
	for line in fin:
		line = line.split()
		if line[1] in subreddit_counts.keys():
			subreddit_counts[line[1]] += 1
		else:
			subreddit_counts[line[1]] = 1
	fin.close()

	top_subreddits = list(sorted(subreddit_counts, key=subreddit_counts.__getitem__, reverse=True))[:max_subreddits]

	fin = open(infile, 'r')
	fout = open(outfile, 'w')
	for line in fin:
		if line.split()[1] in top_subreddits:
			fout.write("%s" % line)	
	fin.close()
	fout.close()

def count_class_members(infile, div_point):
	'''
		Takes a file that contains lines of (account_id, subreddit_id, affinity) tuples,
		and calculates the number of members in each class (affinity > div_point and the
		rest).
	'''

	fin = open(infile, 'r')
	true_class_count = 0
	false_class_count = 0

	for line in fin:
		if float(line.split()[2]) > div_point:
			true_class_count += 1
		else:
			false_class_count += 1
	fin.close()

	print 'True class = ' + str(true_class_count)
	print 'False class = ' + str(false_class_count)

if __name__ == '__main__':
#	sort_dump('../../datasets/publicvotes-20101018_votes.dat', '../../datasets/publicvotes-sorted.dat', 10000000)
	filter_top_subreddits('../../datasets/affinities-1e7-scale.dat', '../../datasets/affinities-150sr-scale.dat', 150)
#	count_class_members('../../datasets/affinities-100sr-shuffled-scale.dat', 0.5)
