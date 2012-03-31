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

if __name__ == '__main__':
#	sort_dump('../../datasets/publicvotes-20101018_votes.dat', '../../datasets/publicvotes-sorted.dat', 10000000)
	filter_top_subreddits('../../datasets/affinities-1e7.dat', '../../datasets/affinities-100sr.dat', 100)
