(* author: Alessandro Peria *)
(* code review: Łukasz Łopacki *)

type point = float * float;;
type kartka = point -> int;;

type t = Left | On | Right;;

let epsilon = 0.00000001;;

let side (x1,y1) (x2,y2) (xp, yp) = 
  let s = x1 *. y2 -. x2 *. y1 +. x2 *. yp -. xp *. y2 +. xp *. y1 -. x1 *. yp in
  if s > epsilon then Left
  else if -.epsilon <= s && s <= epsilon then On
  else Right;; 

let reflection (x1, y1) (x2, y2) (xp, yp) = 
  let (plusX, plusY) = 
    if x1 = x2 then (x1 -. xp, 0.)
    else if y1 = y2 then (0., y1 -. yp)  
    else 
      let a = (y2 -. y1) /. (x2 -. x1) in
      let b = y1 -. a *. x1 in
      let a1 = -1. /. a in
      let b1 = yp -. a1 *. xp in 
      let xk = (b1 -. b) /. (a -. a1) in 
      let yk = a *. xk +. b in
      (xk -. xp, yk -. yp)
  in
  (xp +. 2. *. plusX, yp +. 2. *. plusY);;
  
let zloz p1 p2 kartka p = 
  match side p1 p2 p with
  | Left -> (kartka p) + kartka (reflection p1 p2 p)
  | On -> kartka p
  | Right -> 0;;

let prostokat (x1, y1) (x2, y2) (xp, yp) = 
  if x1 <= xp +. epsilon && xp -. epsilon <= x2 && y1 <= yp +. epsilon && yp -. epsilon <= y2 then 1
  else 0;;

let distSquare (x1,y1) (x2,y2) =
  (x1 -. x2) *. (x1 -. x2) +. (y1 -. y2) *. (y1 -. y2);;

let kolko center r p = 
  if distSquare center p <= r *. r +. epsilon then 1
  else 0;;

let skladaj lista k = 
  List.fold_left (fun a (p1, p2) -> zloz p1 p2 a) k lista;; 
