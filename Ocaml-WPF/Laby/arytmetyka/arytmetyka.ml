type zbior =
	{l:float; p:float};;
type wartosc = 
	{st: zbior; nd:zbior};;
let isIn z el = 
	if (classify_float z.l) = FP_nan then false
	else if z.l <= el && el <= z.p then true
		 else false;;
let getFull = 
	let z:zbior = {l = neg_infinity; p = infinity} in
	{st = z; nd = z};;
let wartosc_dokladnosc x p = 
	let roznica = (x *. p) /. 100. in 
	let v1 = x -. roznica in
	let v2 = x +. roznica in
	let z:zbior = {l = min v1 v2;p = max v1 v2} in
	let wart:wartosc = {st=z;nd=z} in
	wart;;
let wartosc_od_do x y =
	if y = 0. && x < 0. then
			let z:zbior = {l=x;p= -.y} in
			let wart:wartosc = {st=z;nd=z} in
			wart
	else
			let z:zbior = {l=x;p=y} in 
			let wart:wartosc = {st=z;nd=z} in
			wart;;
let wartosc_dokladna x = 
	let z:zbior = {l=x;p=x} in
	let wart:wartosc = {st=z;nd=z} in
	wart;;
let in_wartosc x y = 
	let z1:zbior = x.st in 
	let z2:zbior = x.nd in 
	isIn z1 y || isIn z2 y;; 
let min_wartosc x = 
	x.st.l;;
let max_wartosc x =
	x.nd.p;;
let sr_wartosc x = 
	(min_wartosc x +. max_wartosc x) /. 2.;;
let dodaj z1 z2 =
	let z:zbior = {l=z1.l +. z2.l; p = z1.p +. z2.p} in
	z;;
let obieInf a = 
	a.st.l = neg_infinity && a.nd.p = infinity;;
let plus a b =
	if obieInf a && obieInf b then 
			getFull
	else
			let z_a1 = a.st in
			let z_a2 = a.nd in
			let z_b1 = b.st in
			let z_b2 = b.nd in
			if obieInf a then
					{st=dodaj z_a1 z_b1; nd=dodaj z_a2 z_b1}
			else
					{st=dodaj z_b1 z_a1; nd = dodaj z_b2 z_a2};;
let minus a b =
	let z_b1 = b.st in
	let z_b2 = b.nd in
	let przeciw_z z = 
			{l = -.z.p; p = -.z.l} in
	let neg_b = {st=przeciw_z z_b2; nd = przeciw_z z_b1} in
	plus a neg_b;; 
let isFull a = 
	(a.st.l = neg_infinity && a.st.p = infinity);;
let normFull a = 
	if a.st.l = neg_infinity && a.nd.p = infinity && a.st.p >= a.nd.l then getFull
	else
			a;;
let norm a =
	if a.st.p >= a.nd.l-.0.0000000001 then
      getFull
	else
			a;;
			
let czyDokladnieZero a = 
	let epsilon = 0.00000001 in
	((abs_float (a.st.l -. a.st.p)) <= epsilon && abs_float a.st.l <= epsilon);;
let isZbiorNan z =
	(classify_float z.l = FP_nan || classify_float z.p = FP_nan);;
let isWartNan a = 
	(isZbiorNan a.st || isZbiorNan a.nd);;
let getNanZ = 
	{l=nan;p=nan};;
let getNanWart =
	{st=getNanZ;nd=getNanZ};;
let mojMin a b =
	if classify_float a = FP_nan then b
	else
			if classify_float b = FP_nan then a
			else
					min a b;;
let mojMax a b =
	if classify_float a = FP_nan then b
	else
			if classify_float b = FP_nan then a
			else
					max a b;;
let razy_zbiory za zb = (* mnoży spójne zbiory*)
	let v1 = za.l *. zb.l in
	let v2 = za.l *. zb.p in
	let v3 = za.p *. zb.l in
	let v4 = za.p *. zb.p in
	{l=mojMin (mojMin v1 v2) (mojMin v3 v4); p = mojMax (mojMax v1 v2) (mojMax v3 v4)};;
let normKolejnosc w =
	if w.nd.l < w.st.l then
			{st=w.nd; nd=w.st}
	else
			w;;
let getZeroZ =
	{l=0.;p=0.};;
let getZeroW = 
	{st = getZeroZ;nd = getZeroZ};;
let rec razy a b = (* uwzględniam mnożenie przez dokładnie zero *)
	if isWartNan a || isWartNan b then getNanWart else
	if obieInf a && obieInf b then
			if isFull a && isFull b then getFull else
			let v1 = razy {st = a.st; nd = a.st} b in 
			let v2 = razy {st = a.nd; nd = a.nd} b in
			let first = {l = neg_infinity; p = max v1.st.p v2 .st.p} in
			let second = {l = min v1.nd.l v2.nd.l; p = infinity} in
			normFull {st = first; nd = second}
	else 
			if obieInf a then razy b a
			else
					if czyDokladnieZero a then getZeroW
					else
							let z1 = razy_zbiory a.st b.st in
							let z2 = razy_zbiory a.st b.nd in
							normFull (normKolejnosc {st=z1;nd=z2});;
let minusZero =1./. neg_infinity ;;
let odwrP b =
        let prawy = b.st.p in
        if prawy = 0.0 then 1.0 /. minusZero
        else
                1.0 /. prawy;;
let odwrotnosc b =
        if isFull b then b else 
	let lewy = b.nd.l in
        let odwrP = odwrP b in
        let odwrL = 1./.lewy in
	if odwrP <= odwrL then 
			let z = {l=odwrP; p =odwrL} in
			{st=z;nd=z}
	else 
			norm {st={l=neg_infinity;p=odwrL}; nd={l=odwrP; p=infinity}};;
let podzielic a b = 
	if czyDokladnieZero b then getNanWart
	else 
			if isFull b then getFull
			else razy (odwrotnosc b) a;;
let epsilon = 0.000001;;

let is_nan x =
		match classify_float x with
			| FP_nan -> true
				| _ -> false;;

let is_inf x = x = infinity;;

let is_neg_inf x = x = neg_infinity;;

let is_eq x y = abs_float (x -. y) < epsilon;;

