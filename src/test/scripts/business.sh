set -e

BASEURL='localhost:8080'

rq() {
  user=$1
  method=$2
  url="$BASEURL$3"
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
part_id=$(rq our_supplier PUT "/part?initiallyAvailable=3")
echo "Part id is $part_id"


# Buy some parts on behalf of customer
order_id=$(rq some_customer PUT "/order?partId=$part_id")
# ...pay for order...
recv_part_id=$(rq some_customer PUT "/order/$order_id/confirm?proofOfPayment=123")
[ "$part_id" == "$recv_part_id" ]

# Reserve two parts at once, then pay for both
order_id1=$(rq some_customer PUT "/order?partId=$part_id")
order_id2=$(rq some_customer PUT "/order?partId=$part_id")
recv_part_id1=$(rq some_customer PUT "/order/$order_id1/confirm?proofOfPayment=66")
recv_part_id2=$(rq some_customer PUT "/order/$order_id2/confirm?proofOfPayment=77")
[ "$part_id" == "$recv_part_id1" ]
[ "$part_id" == "$recv_part_id2" ]


# Retire some parts on behalf of supplier, confirming that all have been bought
rq our_supplier DELETE "/part/$part_id"
