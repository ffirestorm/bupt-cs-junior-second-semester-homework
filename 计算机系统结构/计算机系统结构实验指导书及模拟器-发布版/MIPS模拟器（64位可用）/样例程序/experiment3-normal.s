.text
main:
	ADDIU $r4, $r0, a    # $r4 = &a[0]
	ADDIU $r5, $r0, b    # $r5 = &b[0]
	ADDIU $r6, $r0, n    # $r6 = n    
	BGEZAL $r0, func	 # 函数调用
	NOP
	TEQ $r0, $r0

func:
	# initial res
	ADDIU $r7, $r0, 0    # $r7 = res
	ADDIU $r9, $r0, 0    # 循环的下标
loop:
	LW $r10, 0($r4)   		# $r10 = a[i]
	LW $r11, 0($r5)   		# $r11 = b[i]
	MUL $r10, $r10, $r11   	# $r10 = a[i] * b[i]
	ADD $r7, $r7, $r10   	# res += a[i] * b[i]
	ADDIU $r4, $r4, 4   	# r4地址指向下一个
	ADDIU $r5, $r5, 4   	# r5指向下一个地址
	ADDIU $r9, $r9, 1   	# 索引自增
	BNE $r9, $r6, loop
	JR $r31        			# 返回值
	
.data
	a: .word 1, 2, 3, 4, 5, 6, 7, 8, 9   # 数组 a[]
	b: .word 9, 8, 7, 6, 5, 4, 3, 2, 1   # 数组 b[]
	n: .word 9                           # 给 n赋值
	res: .word 0                         # 给 res初始化
