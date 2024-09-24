.text
main:
ADDIU  $r1,$r0,A
MUL    $r24,$r26,$r14
LW     $r2,0($r1)
MUL    $r12,$r10,$r1 	# r12 往前提
LW     $r6,4($r1)	 # 先把r1的东西存到r6里，因为r1和r6都不会动
ADD    $r4,$r0,$r2    
ADD    $r16,$r12,$r1  
ADD    $r8,$r6,$r1 
SW     $r4,0($r1)	 # 得放到对r4的操作后三个位置才不冲突，因为要等r4写回才能store回去
LW     $r20,8($r1)
SW     $r18,16($r1)
ADD    $r18,$r16,$r1 # 原本SW和MUL之间有冲突，插入一条无关指令后不冲突了
MUL    $r22,$r20,$r14
TEQ $r0,$r0

.data
A: 
.word 4,6,8
