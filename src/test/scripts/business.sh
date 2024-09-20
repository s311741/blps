set -e

rq() {
  user=$1
  port=$2
  method=$3
  url="localhost:$port$4"
  set -e
  >&2 echo "Performing $method on $url"
  result=$(\
    wget --method=$method \
    --user=$user --password=password \
    "$url" \
    -O - \
  )
  >&2 echo "Result was: '$result'"
  echo $result
}

# Setup some parts on behalf of supplier
part_id=$(rq our_supplier 8090 PUT "/part?initiallyAvailable=3")
echo "Part id is $part_id"


# Buy some parts on behalf of customer
order_id=$(rq some_customer 8080 PUT "/order?partId=$part_id")
# ...pay for order...
recv_part_id=$(rq some_customer 8080 PUT "/order/$order_id/confirm?proofOfPayment=123")
[ "$part_id" == "$recv_part_id" ]

# Reserve two parts at once, then pay for both
order_id1=$(rq some_customer 8080 PUT "/order?partId=$part_id")
order_id2=$(rq some_customer 8080 PUT "/order?partId=$part_id")
recv_part_id1=$(rq some_customer 8080 PUT "/order/$order_id1/confirm?proofOfPayment=66")
recv_part_id2=$(rq some_customer 8080 PUT "/order/$order_id2/confirm?proofOfPayment=77")
[ "$part_id" == "$recv_part_id1" ]
[ "$part_id" == "$recv_part_id2" ]