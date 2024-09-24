.text
main:
	ADDIU $r5, $r0, n    # r5存n的地址
	ADDIU $r8, $r0, a    # r8放入a的地址
	LW $r5, 0($r5)       # r5存n的值
	BGEZAL $r0, bubble	 # 跳到bubble排序
	NOP
	TEQ $r0, $r0         # 结束
bubble:
	ADDIU $r7, $r5, -1	 # r7 先存n-1
	BLEZ $r7, return     # if n<=1 return
	SLL $r5, $r5, 2		 # r5 左移两位, 因为后面要用到长度*4
	ADDU $r6, $r8, $r5   # r6存最后的地址，用来检查是否越界
outer_loop:  
	ADDIU $r2, $r8, 4    # r2存a[0]
inner_loop:
	LW $r3, -1($r2)		 # r3 存a[j]
	LW $r4, 0($r2)		 # r4 存a[j+1]
	BLT $r4, $r3, check
	SW $r4, -1($r2)		 # 交换元素
	SW $r3, 0($r2)		 # 交换
check:
	ADDIU $r2, $r2, 4    # 索引++
	BNE $r6, $r2, inner_loop # 判断内循环
	ADDIU $r7, $r7, -1
	ADDIU $r6, $r6, -4
	BNE $r7, $r0, outer_loop # 外循环
return:
	JR $r31
.data
	a: .word 5, 7, 10, 8, 4, 2, 1, 3, 6, 9
	n: .word 10