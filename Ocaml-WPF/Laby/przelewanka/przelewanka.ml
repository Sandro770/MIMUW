(* author: Alessandro Peria *)
(* review: Maksym Ratajczyk *)

module Q = Queue;;
let przelewanka input = 
  let inputList = Array.to_list input in 
  (* removing bottles with 0 capacity*)
  let correctedList = List.filter (fun (x, _) -> x <> 0) inputList in  
  let input = Array.of_list correctedList in 
  
  if input = [||] then 0 else
  
  let rec gcd a b = 
    if a = 0 then b else gcd (b mod a) a in
  
  let split a =
    let cap = Array.map (fun (x, _) -> x) a in
    let exp = Array.map (fun (_, y) -> y) a in
    (cap, exp) in
  
  let (capacities, expected) = split input in
  let toDivide = Array.fold_left gcd 0 capacities in
  if Array.exists (fun exp -> (exp mod toDivide <> 0)) expected then -1
  else 
    (* preprocessed input*)
    let capacities = Array.map (fun x -> x / toDivide) capacities in
    let expected = Array.map (fun x -> x / toDivide) expected in 
    
    let q = Q.create () in
    
    let nbOfGlasses = Array.length input in 
    let allEmpty = Array.make nbOfGlasses 0 in
    
    Hashtbl.randomize ();
    let htbl = Hashtbl.create 30 in 
    
    let ans = ref (-1) in
    
    let tryToAddVertex x dist = 
      if (Hashtbl.mem htbl x) = false then begin 
        let x = Array.copy x in 
        if x = expected then begin (* checks if we found answer *) 
          ans := dist; 
          Q.clear q (* ends bfs *)
          end
        else begin 
          (Q.add (x, dist) q);
          (Hashtbl.add htbl x ());
        end
      end      
    in
    
    tryToAddVertex allEmpty 0; 

    let czyKoniec = ref true in (* taking care of a case without solution *)
    Array.iteri (fun i e -> if e = 0 || e = capacities.(i) then czyKoniec := false) expected;
    if !czyKoniec then -1 
    else begin   
      while Q.is_empty q = false do (* bfs through all possible configurations *)
        let (curState, curDist) = Q.take q in
        
        let addNeighs index curHeight = if !ans = (-1) then begin 
          let nei = Array.copy curState in
          
          nei.(index) <- 0; (* clears index-th bottle*)
          tryToAddVertex nei (curDist + 1);       
            
          nei.(index) <- capacities.(index); (* fills index-th bottle *)
          tryToAddVertex nei (curDist + 1);
            
          nei.(index) <- curHeight;
              
          let przelej index2 _ = begin (* pours water from bottle index-th to bottle index2-th  *)
            let amount = min curState.(index) (capacities.(index2) - curState.(index2)) in      
            nei.(index) <- nei.(index) - amount;
            nei.(index2) <- nei.(index2) + amount;
            tryToAddVertex nei (curDist + 1);
            nei.(index) <- nei.(index) + amount;
            nei.(index2) <- nei.(index2) - amount
            end
          in
      
          Array.iteri przelej curState
          end 
        in
        Array.iteri addNeighs curState; 
      done;
    !ans
    end
;;
 
