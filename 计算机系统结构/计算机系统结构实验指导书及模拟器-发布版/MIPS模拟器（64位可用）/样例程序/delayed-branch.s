.text
main:
ADDI  $r2,$r0,1024
ADD   $r3,$r0,$r0
ADDI  $r4,$r0,8
# 这里可以先do loop的第一条
LW    $r1,0($r2)
loop:  
ADDI  $r1,$r1,1
ADDI  $r3,$r3,4
SW    $r1,0($r2) 		# 这里下移，减少r1的数据冲突
SUB   $r5,$r4,$r3
BGTZ  $r5,loop			# 跳转到loop,loop先做LW $r1,0($r2)
ADD   $r7,$r0,$r6
# 此处可以放上loop最开始要做的事情
LW    $r1,0($r2)
TEQ   $r0,$r0