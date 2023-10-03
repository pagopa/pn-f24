echo "### CREATE TABLES FOR F24 ###"

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name pn-F24Metadata \
    --attribute-definitions \
        AttributeName=setId,AttributeType=S \
    --key-schema \
        AttributeName=setId,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

aws --profile default --region us-east-1 --endpoint-url=http://localstack:4566 \
    dynamodb create-table \
    --table-name pn-F24File \
    --attribute-definitions \
        AttributeName=pk,AttributeType=S \
    --key-schema \
        AttributeName=pk,KeyType=HASH \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5

echo "### CREATE QUEUES FOR F24 ###"
queues="pn-f24_internal pn-safestore_to_f24"
for qn in $(echo $queues | tr " " "\n"); do
  echo creating queue $qn ...
  aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
    sqs create-queue \
    --attributes '{"DelaySeconds":"2"}' \
    --queue-name $qn
done

echo "### CREATE QUEUES FOR DELIVERY-PUSH ###"
queues="pn-f24_to_deliverypush"
for qn in $(echo $queues | tr " " "\n"); do
  echo creating queue $qn ...
  aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
    sqs create-queue \
    --attributes '{"DelaySeconds":"2"}' \
    --queue-name $qn
done

echo "### CREATE QUEUES FOR PAPER-CHANNEL ###"
queues="pn-f24_to_paperchannel"
for qn in $(echo $queues | tr " " "\n"); do
  echo creating queue $qn ...
  aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
    sqs create-queue \
    --attributes '{"DelaySeconds":"2"}' \
    --queue-name $qn
done

echo "### CREATE EVENT BUS - pn-CoreEventBus ###"
event_bus_name="pn-CoreEventBus"
aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
  events create-event-bus --name $event_bus_name


echo "### CREATE RULE FOR DELIVERY-PUSH ###"
rule_name_delivery_push="f24_to_deliverypush"
pattern='{"source": ["pn-f24"], "detail-type": ["F24OutcomeEvent"], "detail": {"clientId": ["pn-delivery-push-f24"]}}'
aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
  events put-rule --name $rule_name_delivery_push --event-pattern "$pattern" \
  --event-bus-name $event_bus_name


echo "### ENABLE RULE DELIVERY-PUSH ###"
 aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
  events enable-rule --name $rule_name_delivery_push \
  --event-bus-name $event_bus_name

echo "### ADD TARGET TO RULE DELIVERY-PUSH ###"
  target_arn="arn:aws:sqs:us-east-1:000000000000:pn-f24_to_deliverypush"
 aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
    events put-targets --rule $rule_name_delivery_push \
    --targets "Id"="1","Arn"="$target_arn" \
    --event-bus-name $event_bus_name

echo "### CREATE RULE FOR PAPER-CHANNEL ###"
      rule_name_paper_channel="f24_to_paperchannel"
      pattern='{"source": ["pn-f24"], "detail-type": ["F24OutcomeEvent"], "detail": {"clientId": ["pn-paper-channel-f24"]}}'
 aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
        events put-rule --name $rule_name_paper_channel --event-pattern "$pattern" \
        --event-bus-name $event_bus_name

echo "### ENABLE RULE PAPER-CHANNEL ###"
aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
    events enable-rule --name $rule_name_paper_channel \
    --event-bus-name $event_bus_name

echo "### ADD TARGET TO RULE ###"
  target_arn="arn:aws:sqs:us-east-1:000000000000:pn-f24_to_paperchannel"
aws --profile default --region us-east-1 --endpoint-url http://localstack:4566 \
    events put-targets --rule $rule_name_paper_channel \
    --targets "Id"="1","Arn"="$target_arn" \
    --event-bus-name $event_bus_name


echo "Initialization terminated"