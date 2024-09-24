.text
main:
	ADDIU $r4, $r0, a    # $r4 = &a[0]
	ADDIU $r5, $r0, b    # $r5 = &b[0]
	ADDIU $r6, $r0, n    # $r6 = n    
	BGEZAL $r0, func	 # 函数调用
	NOP
	TEQ $r0, $r0

func:
	ADDIU $r7, $r0, 0    # $r7 = res
	ADDIU $r9, $r0, 0    # 循环的下标
loop:
	LW $r10, 0($r4)   		# $r10 = a[i]
	LW $r11, 0($r5)   		# $r11 = b[i]
	# 将 无关的ADDIU提前，因为r4、r5没有冲突
	ADDIU $r4, $r4, 4   	# r4地址指向下一个
	ADDIU $r5, $r5, 4   	# r5指向下一个地址
	# MUL需要r11的数据，因此可以放后面一点
	# 将r10和r11的操作结果放到另一个寄存器里，减少冲突
	MUL $r12, $r10, $r11   	# $r12 = a[i] * b[i]
	# 将对r9的操作也提前
	ADDIU $r9, $r9, 1   	# 索引自增
	ADD $r7, $r7, $r12   	# res += a[i] * b[i]
	BNE $r9, $r6, loop
	JR $r31        			# 返回值

.data
	a: .word 1, 2, 3, 4, 5, 6, 7, 8, 9   # 数组 a[]
	b: .word 9, 8, 7, 6, 5, 4, 3, 2, 1   # 数组 b[]
	n: .word 9                           # 给 n赋值