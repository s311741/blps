set -e

USER=user
PASSWORD=user

BASEURL='localhost:8080'

rq() {
  method=$1
  url="$BASEURL$2"
  set -e
  >&2 echo "Performing $method on $url"
  result=$(wget --method=$method \
    --user=$USER --password=$PASSWORD \
    "$url" \
    -O - \
    2>/dev/null)
  >&2 echo "Result was: '$result'"
  echo $result
}

# Setup some parts on behalf of supplier
part_id=$(rq PUT "/part?initiallyAvailable=3")
echo "Part id is $part_id"


# Buy some parts on behalf of customer
order_id=$(rq PUT "/order?partId=$part_id")
# ...pay for order...
recv_part_id=$(rq PUT "/order/confirm?id=$order_id&proofOfPayment=123")
[ "$part_id" == "$recv_part_id" ]

# Reserve two parts at once, then pay for both
order_id1=$(rq PUT "/order?partId=$part_id")
order_id2=$(rq PUT "/order?partId=$part_id")
recv_part_id1=$(rq PUT "/order/confirm?id=$order_id1&proofOfPayment=66")
recv_part_id2=$(rq PUT "/order/confirm?id=$order_id2&proofOfPayment=77")
[ "$part_id" == "$recv_part_id1" ]
[ "$part_id" == "$recv_part_id2" ]


# Retire some parts on behalf of customer, confirming that all have been bought
rq DELETE "/part?id=$part_id"
