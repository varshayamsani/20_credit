from typing import TypedDict
from langgraph.graph import StateGraph, START, END



# STATE


class LeaveState(TypedDict):
    employee_name: str
    employee_id: str
    leave_type: str
    leave_days: int
    reason: str
    leave_balance: int

    status: str
    rejection_reason: str

    manager_decision: str
    hr_decision: str

    employee_action: str



# NODES


VALID_LEAVE_TYPES = [
    "Casual",
    "Sick",
    "Earned",
    "Maternity"
]


def validate_request(state: LeaveState):

    if not state.get("employee_name"):
        return {
            "status": "REJECTED",
            "rejection_reason": "Employee name missing"
        }

    if not state.get("employee_id"):
        return {
            "status": "REJECTED",
            "rejection_reason": "Employee ID missing"
        }

    if state.get("leave_days", 0) <= 0:
        return {
            "status": "REJECTED",
            "rejection_reason": "Invalid leave days"
        }

    if state.get("leave_type") not in VALID_LEAVE_TYPES:
        return {
            "status": "REJECTED",
            "rejection_reason": "Invalid leave type"
        }

    if not state.get("reason"):
        return {
            "status": "REJECTED",
            "rejection_reason": "Reason cannot be empty"
        }

    return {}


def check_leave_balance(state: LeaveState):

    if state["leave_days"] > state["leave_balance"]:
        return {
            "status": "REJECTED",
            "rejection_reason": "Insufficient Leave Balance"
        }

    return {}


def auto_approve(state: LeaveState):

    print("\nAUTO APPROVED\n")

    return {
        "status": "APPROVED"
    }


def manager_approval(state: LeaveState):

    print("\nMANAGER APPROVAL REQUIRED")

    decision = input(
        "Manager Decision (approve/reject): "
    ).strip().lower()

    return {
        "manager_decision": decision
    }


def hr_approval(state: LeaveState):

    print("\nHR APPROVAL REQUIRED")

    decision = input(
        "HR Decision (approve/reject): "
    ).strip().lower()

    return {
        "hr_decision": decision
    }


def final_approval(state: LeaveState):

    print("\nLEAVE APPROVED\n")

    return {
        "status": "APPROVED"
    }


def resubmission(state: LeaveState):

    print("\nREQUEST REJECTED")

    action = input(
        "Employee Action (modify/cancel): "
    ).strip().lower()

    updates = {
        "employee_action": action
    }

    if action == "modify":

        print("\nModify Leave Request")

        updates["leave_days"] = int(
            input("New Leave Days: ")
        )

        updates["leave_balance"] = int(
            input("Leave Balance: ")
        )

    return updates


def cancel_request(state: LeaveState):

    print("\nREQUEST CANCELLED\n")

    return {
        "status": "CANCELLED"
    }


def reject_request(state: LeaveState):

    print(
        f"\nREJECTED: {state.get('rejection_reason', 'Request Rejected')}\n"
    )

    return {}



# ROUTERS


def validation_router(state: LeaveState):

    if state.get("status") == "REJECTED":
        return "reject"

    return "balance"


def balance_router(state: LeaveState):

    if state.get("status") == "REJECTED":
        return "reject"

    days = state["leave_days"]

    if days <= 2:
        return "auto"

    if days <= 5:
        return "manager"

    return "hr"


def manager_router(state: LeaveState):

    if state["manager_decision"] == "approve":
        return "approved"

    return "resubmit"


def hr_router(state: LeaveState):

    if state["hr_decision"] == "approve":
        return "approved"

    return "resubmit"


def resubmit_router(state: LeaveState):

    if state["employee_action"] == "modify":
        return "validate"

    return "cancel"



# BUILD GRAPH

builder = StateGraph(LeaveState)

builder.add_node("validate", validate_request)
builder.add_node("balance", check_leave_balance)
builder.add_node("auto", auto_approve)
builder.add_node("manager", manager_approval)
builder.add_node("hr", hr_approval)
builder.add_node("approved", final_approval)
builder.add_node("resubmit", resubmission)
builder.add_node("cancel", cancel_request)
builder.add_node("reject", reject_request)

builder.add_edge(START, "validate")

builder.add_conditional_edges(
    "validate",
    validation_router,
)

builder.add_conditional_edges(
    "balance",
    balance_router,
)

builder.add_conditional_edges(
    "manager",
    manager_router,
)

builder.add_conditional_edges(
    "hr",
    hr_router,
)

builder.add_conditional_edges(
    "resubmit",
    resubmit_router,
)

builder.add_edge("reject", END)
builder.add_edge("approved", END)
builder.add_edge("cancel", END)

builder.add_edge("validate", "balance")

graph = builder.compile()



# TEST INPUT

input_data = {
    "employee_name": "Varsha",
    "employee_id": "EMP001",
    "leave_type": "Casual",
    "leave_days": 2,
    "reason": "Family Function",
    "leave_balance": 2,

    "status": "",
    "rejection_reason": "",

    "manager_decision": "",
    "hr_decision": "",

    "employee_action": ""
}


result = graph.invoke(input_data)

print("\nFINAL STATE")
print(result)
