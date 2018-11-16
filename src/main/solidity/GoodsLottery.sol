pragma solidity ^0.4.24;

contract GoodsLottery {

    address supplier;
    uint minPayableAmount;
    uint minLotteryTotal;
    uint biddingEnd;

    uint participantsNumber;
    string winningHash;

    string lotDescription;
    mapping(address => uint) participantsInput;
    address[] participants;

    uint winningMemberIndex;
    address winner = address(0x0);
    LotteryState state;
    mapping(address => bool) approvedDelivery;

    modifier onlyBefore(uint _time) {require(now < _time);
        _;}
    modifier onlyOwner(string message) {require(supplier == msg.sender, message);
        _;}
    modifier withValue() {require(msg.value > minPayableAmount, "Please enter more than minimal lottery amount");
        _;}
    modifier notOwner() {require(supplier != msg.sender, "Can not participate in own auction");
        _;}
    modifier oneTimeUsage(){require(participantsInput[msg.sender] == 0x0, "Already in lottery");
        _;
    }
    modifier maxParticipantsValidation() {require(participants.length < participantsNumber, "Number of participants reached");
        _;}

    constructor(string _lotDescription, uint _minPayableAmount, uint _minLotteryTotal, uint _biddingEnd, uint _participantsNumber,
        string _winningHash
    ) public {
        /*        require(bytes(_lotDescription).length < 30, "Description is to long");
                require(bytes(_winningHash).length < 30, "Hash is to long");*/
        supplier = msg.sender;
        minPayableAmount = _minPayableAmount;
        minLotteryTotal = _minLotteryTotal;
        biddingEnd = _biddingEnd;
        lotDescription = _lotDescription;

        participantsNumber = _participantsNumber;
        winningHash = _winningHash;
        state = LotteryState.ACTIVE;
    }

    function participate()
    public
    payable
    notOwner()
    oneTimeUsage()
    onlyBefore(biddingEnd)
    withValue()
    maxParticipantsValidation()
    {
        participantsInput[msg.sender] = msg.value;
        participants.push(msg.sender);
    }

    function getState() view public returns (LotteryState)
    {
        return state;
    }

    function isCompletedCondition() private view returns (bool)
    {
        return participants.length == participantsNumber && address(this).balance > minLotteryTotal;
    }

    function complete(uint winnerIndex) public onlyOwner("Can transfer to creator account only") returns (address)
    {
        require(state != LotteryState.COMPLETED, "Is not completed");
        winningMemberIndex = winnerIndex;
        winner = participants[winnerIndex];
        if (isCompletedCondition()) {
            state = LotteryState.COMPLETED;
        }
        return winner;
    }

    function getWinner() public view returns (address){
        return winner;
    }

    function confirmDeliveryUser() public {
        require(state != LotteryState.CLOSED, "Lottery is already closed");
        require(winner == msg.sender, "Only delivery service or receiver can confirm ");
        transferFunds();
    }


    function refund() public onlyOwner("Can owner can refund") {
        for (uint participantIndex = 0; participantIndex < participants.length; participantIndex++) {
            address participant = participants[participantIndex];
            participant.transfer(participantsInput[participant]);
            participantsInput[participant] = 0;
        }
        state = LotteryState.REFUNDED;
    }

    function transferFunds() private {
        //supplier.transfer(address(this).balance);
        state = LotteryState.CLOSED;
        selfdestruct(supplier);
    }


    enum LotteryState {
        ACTIVE, COMPLETED, CLOSED, REFUNDED
    }

}
