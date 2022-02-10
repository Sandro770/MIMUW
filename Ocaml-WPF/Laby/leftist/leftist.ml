(* author: Alessandro Peria *)
(* code reviewer: Maksym Ratajczyk *) 
(* height[Null] = 0, height[Leaf] = 1 *)
type 'a queue = 
  |Null
  |Node of {l: 'a queue; 
            r: 'a queue; 
            h : int;
            priority: 'a};;

let is_empty q = q = Null;; 

let empty = Null;;

let sortByPriority (Node q1) (Node q2) =
  if q1.priority <= q2.priority then (Node q1, Node q2)
  else (Node q2, Node q1);;

let getH q =
  match q with
  | Null -> 0
  | Node n -> n.h;;

let sortByHeight q1 q2 = 
  if (getH q1) <= (getH q2) then (q2,q1)
  else (q1,q2);;

let rec join q1 q2 =
  if is_empty q1 then q2
  else if is_empty q2 then q1
       else 
         let (Node d1, Node d2) = sortByPriority q1 q2  in
         let cand2 = join d1.r (Node d2) in
         let (left, right) = sortByHeight d1.l cand2 in 
         let newH = 1 + getH right in
         Node {l = left; r = right; h = newH; priority=d1.priority};; 

exception Empty;;

let delete_min q = 
  if is_empty q then raise Empty
  else 
    let Node node = q in
    (node.priority, join node.l node.r);;

let add e q = 
  let single = Node {l = empty; r = empty; h = 1; priority = e} in
  join single q;;
